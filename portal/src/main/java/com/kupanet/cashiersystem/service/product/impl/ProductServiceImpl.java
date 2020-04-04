package com.kupanet.cashiersystem.service.product.impl;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kupanet.cashiersystem.DAO.ProductMapper;
import com.kupanet.cashiersystem.model.BloomRetryEvent;
import com.kupanet.cashiersystem.model.Product;
import com.kupanet.cashiersystem.service.mq.CanalDatabaseEventConsumer;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Value("${cashier.rocketmq.canalDatabaseTopic}")
    private String canalDatabaseTopic;

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public int create( Product productParam) {
        Product product = productParam;
        product.setId(null);
        return productMapper.insert(product);
    }

    @Override
    public int update(Long id, Product productParam) {
        Product product = productParam;
        product.setId(id);
        return productMapper.updateById(product);
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
        QueryWrapper queryWrapper = new QueryWrapper();
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
        Product product;
        if((product= (Product) redisUtil.get(CanalDatabaseEventConsumer.generateRedisKey(String.valueOf(id))))
                !=null){
            return product;
        }else {
            product = productMapper.selectById(id);
            FlatMessage flatMessage = new CanalUtil<Product>().convertFlatMessageFromObject(product);
            if(flatMessage!=null&&flatMessage.getData()!=null){
                rocketMQTemplate.asyncSend(canalDatabaseTopic, flatMessage, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                    }
                    @Override
                    public void onException(Throwable throwable) {
                        logger.error("send delete redis topic fail; {}", throwable.getMessage());
                    }
                });
            }
        }
        return product;
    }

    @Override
    public List<Product> getByCategoryId(Long categoryId) {
        //redisUtil.hget();
        //todo
        return productMapper.selectByCategoryId(categoryId);
    }

    public IPage<Product> page(IPage<Product> page, Wrapper<Product> queryWrapper) {
        queryWrapper = (Wrapper<Product>) SqlHelper.fillWrapper(page, queryWrapper);
        return productMapper.selectPage(page, queryWrapper);
    }

}



