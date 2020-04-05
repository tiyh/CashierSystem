package com.kupanet.cashiersystem.service.mq;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.kupanet.cashiersystem.constant.RedisConstant;
import com.kupanet.cashiersystem.model.BloomRetryEvent;
import com.kupanet.cashiersystem.model.Product;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

/**
 * Receive asynchronously delivered messages orderly. one queue, one thread
 */
@Service
@RocketMQMessageListener(topic = "${cashier.rocketmq.canalDatabaseTopic}",
        consumerGroup = "canal-database-consumer",consumeMode = ConsumeMode.ORDERLY)
public class CanalDatabaseEventConsumer extends AbstractCanalDatabaseEventConsumer<Product> implements RocketMQListener<FlatMessage> {
    private static Logger LOGGER = LoggerFactory.getLogger(CanalDatabaseEventConsumer.class);

    @Override
    public void onMessage(FlatMessage flatMessage) {
        process(flatMessage);
    }

    protected String getModelName(){
        return RedisConstant.PRODUCT_TABLE_NAME;
    }

    @Override
    protected String getIdValue(Product product) {
        return String.valueOf(product.getId());
    }
    public static String generateRedisKey(String id){
        return new StringBuilder()
                .append(RedisConstant.PRODUCT_TABLE_NAME)
                .append("_")
                .append(id)
                .toString();
    }

    public static String generateRedisCategoryKey(Long categoryId){
        return new StringBuilder()
                .append(RedisConstant.PRODUCT_TABLE_NAME)
                .append("_category_")
                .append(categoryId)
                .toString();
    }
    protected String getWrapRedisKey(Product product) {
        return generateRedisKey(getIdValue(product));

    }
    @Override
    protected void redisInsert( Map<String,Product> columns){
        Random r = new Random();
        int randomInt = 120+r.nextInt(60);
        for (Map.Entry<String,Product> column : columns.entrySet()) {
            Product product = column.getValue();
            LOGGER.info("redisInsert getWrapRedisKey:{},column:{}",getWrapRedisKey(column.getValue()),column.getKey());
            redisUtil.set(getWrapRedisKey(product),column.getKey(),randomInt);
            if(!redisUtil.addBloom(getModelName(),String.valueOf(product.getId()))){
                rocketMQTemplate.asyncSend(bloomTopic, MessageBuilder.withPayload(new BloomRetryEvent(getModelName(),String.valueOf(getIdValue(column.getValue())))).build(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        if(!redisUtil.addBloom(getModelName(),String.valueOf(product.getId()))){
                            LOGGER.error("send delete redis topic fail; {}", throwable.getMessage());
                        }
                    }
                },rocketMQTemplate.getProducer().getSendMsgTimeout(),DELAY_LEVEL);
            }
            Long categoryId=product.getProductCategoryId();
            if(categoryId!=null && categoryId>0){
                redisUtil.sSet(generateRedisCategoryKey(categoryId),product.getId());
            }
        }
    }
    @Override
    protected void redisUpdate( Map<String,Product> columns,Map<String,Product> old){
        Random r = new Random();
        int randomInt = 120+r.nextInt(60);
        for (Map.Entry<String,Product> column : columns.entrySet()) {
            Product newProduct = column.getValue();
            LOGGER.info("redisUpdate getWrapRedisKey:{},column:{}",getWrapRedisKey(newProduct),column.getKey());
            redisUtil.set(getWrapRedisKey(newProduct),column.getKey(),randomInt);
            Long categoryId=newProduct.getProductCategoryId();
            if(categoryId!=null && categoryId>0){
                Product oldProduct = old.get(column.getKey());
                if(oldProduct!=null&& !categoryId.equals(oldProduct.getProductCategoryId()))
                    redisUtil.sSet(generateRedisCategoryKey(categoryId),newProduct.getId());
            }
        }
    }
}

