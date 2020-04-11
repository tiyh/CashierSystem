package com.kupanet.cashiersystem.service.product.impl;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlHelper;
import com.kupanet.cashiersystem.DAO.ProductMapper;
import com.kupanet.cashiersystem.constant.RedisConstant;
import com.kupanet.cashiersystem.model.Product;
import com.kupanet.cashiersystem.model.RedisRetryEvent;
import com.kupanet.cashiersystem.service.mq.CanalProductEventConsumer;
import com.kupanet.cashiersystem.service.product.ProductService;
import com.kupanet.cashiersystem.util.CanalUtil;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 商品信息 服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Value("${cashier.rocketmq.canalDatabaseTopic}")
    private String canalDatabaseTopic;

    @Value("${cashier.rocketmq.redis.retryTopic}")
    private String redisRetryTopic;
    //messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
    protected final static int DELAY_LEVEL = 1;

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RedisUtil<Product> redisUtil;


    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public int create( Product productParam) {
        productParam.setId(null);
        return productMapper.insert(productParam);
    }

    @Override
    public int update(Long id, Product productParam) {
        productParam.setId(id);
        return productMapper.updateById(productParam);
    }

    @Override
    public int updatePublishStatus(List<Long> ids, Integer publishStatus) {
        return 0;
    }


    @Override
    public int updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
        Product record = new Product();
        record.setRecommandStatus(recommendStatus);

        return productMapper.update(record, new QueryWrapper<Product>().eq("id",ids));
    }

    @Override
    public int updateNewStatus(List<Long> ids, Integer newStatus) {
        Product record = new Product();
        record.setNewStatus(newStatus);

        return productMapper.update(record, new QueryWrapper<Product>().eq("id",ids));
    }

    @Override
    public List<Product> list(String keyword) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_status",0);

        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.like("name",keyword);

        }
        return productMapper.selectList(queryWrapper);
    }

    @Override
    public boolean removeById(Serializable id) {
        return SqlHelper.delBool(productMapper.deleteById(id));
    }


    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return SqlHelper.delBool(productMapper.deleteBatchIds(idList));
    }

    @Override
    public Product getById(Serializable id) {
        if(!redisUtil.existsBloom(RedisConstant.PRODUCT_TABLE_NAME,String.valueOf(id))){
            return null;
        }
        Product product;
        if((product=
                redisUtil.getWithMutexKey(
                        CanalProductEventConsumer.generateRedisKey(String.valueOf(id)),
                        generateRedisMutexKey(id),
                        "1",
                        new RedisUtil.RedisCallback<String,Product>(){

                            @Override
                            public Product doCallback(String k) {
                                return getByIdFromDatabase(id);
                            }
                        },Product.class)
            ) != null){
            return product;
        }else {
            product = getByIdFromDatabase(id);
        }
        return product;
    }
    private Product getByIdFromDatabase(Serializable id){
        Product product = productMapper.selectById(id);
        if(product==null) return null;
        FlatMessage flatMessage = new CanalUtil<Product>().convertFlatMessageFromObject(product);
        if(flatMessage!=null&&flatMessage.getData()!=null){
            rocketMQTemplate.asyncSendOrderly(canalDatabaseTopic, MessageBuilder.withPayload(flatMessage).build(),CanalProductEventConsumer.generateRedisKey(String.valueOf(id)),new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                }
                @Override
                public void onException(Throwable throwable) {
                    logger.error("send delete redis topic fail; {}", throwable.getMessage());
                }
            });
        }
        return product;
    }

    @Override
    public List<Product> getByCategoryId(Long categoryId) {
        List<Product> lp = new LinkedList<>();
        Set<Object> ids = redisUtil.sGet(CanalProductEventConsumer.generateRedisCategoryKey(categoryId));
        if(ids!=null&& !ids.isEmpty()){
            for(Object ob: ids){
                Product p = getById((Long)ob);
                if(categoryId.equals(p.getProductCategoryId())) {
                    lp.add(p);
                }else {
                    redisUtil.setRemove(CanalProductEventConsumer.generateRedisCategoryKey(categoryId),p.getId());
                    Long newCId =p.getProductCategoryId();
                    if(newCId!=null && newCId!=0){
                        redisUtil.sSet(CanalProductEventConsumer.generateRedisCategoryKey(p.getProductCategoryId()), p.getId());
                    }
                }
            }
            return lp;
        }
        else{
            lp = productMapper.selectByCategoryId(categoryId);
            for (Product p:lp) {
                if(redisUtil.sSet(CanalProductEventConsumer.generateRedisCategoryKey(categoryId),p.getId())<=0){
                    RedisRetryEvent rre = new RedisRetryEvent.Builder()
                            .key(CanalProductEventConsumer.generateRedisCategoryKey(categoryId))
                            .value(String.valueOf(p.getId()))
                            .commandType(RedisConstant.CommandType.SET_SET)
                            .build();
                    rocketMQTemplate.asyncSend(redisRetryTopic, MessageBuilder.withPayload(rre).build(), new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            if(redisUtil.sSet(CanalProductEventConsumer.generateRedisCategoryKey(categoryId),p.getId())<=0){
                                logger.error("send set_set redis topic fail; {}", throwable.getMessage());
                            }
                        }
                    },rocketMQTemplate.getProducer().getSendMsgTimeout(),DELAY_LEVEL);
                }
            }
        }
        return lp;
    }

    public IPage<Product> page(IPage<Product> page, Wrapper<Product> queryWrapper) {
        queryWrapper = (Wrapper<Product>) SqlHelper.fillWrapper(page, queryWrapper);
        return productMapper.selectPage(page, queryWrapper);
    }
    private String generateRedisMutexKey(Serializable id){
        return RedisConstant.PRODUCT_TABLE_NAME+"_mutex_"+id;
    }

}



