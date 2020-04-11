package com.kupanet.cashiersystem.service.mq;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.kupanet.cashiersystem.constant.RedisConstant;
import com.kupanet.cashiersystem.model.Product;
import com.kupanet.cashiersystem.model.RedisRetryEvent;
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
    public class CanalProductEventConsumer extends AbstractCanalDatabaseEventConsumer<Product> implements RocketMQListener<FlatMessage> {
        private static Logger LOGGER = LoggerFactory.getLogger(com.kupanet.cashiersystem.service.mq.CanalProductEventConsumer.class);

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
            return RedisConstant.DATABASE_NAME+"."+RedisConstant.PRODUCT_TABLE_NAME +
                    ":" + id;
        }

        public static String generateRedisCategoryKey(Long categoryId){
            return  RedisConstant.DATABASE_NAME+"."+RedisConstant.PRODUCT_TABLE_NAME +
                    ".category:" + categoryId;
        }
        protected String getWrapRedisKey(Product product) {
            return generateRedisKey(getIdValue(product));

        }
        @Override
        protected void redisInsert( Map<String,Product> columns){
            Random r = new Random();
            int randomInt = CACHE_EXPIRED_BASE_TIME+r.nextInt(CACHE_EXPIRED_RANDOM_TIME_LIMIT);
            for (Map.Entry<String,Product> column : columns.entrySet()) {
                Product product = column.getValue();
                if(product==null){
                    LOGGER.warn("redisInsert product is null,columns:"+columns.toString());
                    return;
                }
                LOGGER.info("redisInsert getWrapRedisKey:{},column:{}",getWrapRedisKey(column.getValue()),column.getKey());
                redisUtil.set(getWrapRedisKey(product),column.getKey(),randomInt);
                if(!redisUtil.addBloom(getModelName(),String.valueOf(product.getId()))){
                    RedisRetryEvent rre = new RedisRetryEvent.Builder()
                            .key(getModelName())
                            .value(String.valueOf(getIdValue(column.getValue())))
                            .commandType(RedisConstant.CommandType.BlOOM_ADD)
                            .build();
                    rocketMQTemplate.asyncSend(redisRetryTopic, MessageBuilder.withPayload(rre).build(), new SendCallback() {
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
            int randomInt = CACHE_EXPIRED_BASE_TIME+r.nextInt(CACHE_EXPIRED_RANDOM_TIME_LIMIT);
            for (Map.Entry<String,Product> column : columns.entrySet()) {
                Product newProduct = column.getValue();
                if(newProduct==null){
                    LOGGER.warn("redisUpdate newProduct is null,columns:"+columns.toString());
                    return;
                }
                LOGGER.info("redisUpdate getWrapRedisKey:{},column:{}",getWrapRedisKey(newProduct),column.getKey());
                redisUtil.set(getWrapRedisKey(newProduct),column.getKey(),randomInt);
                Long categoryId=newProduct.getProductCategoryId();
                if(categoryId!=null && categoryId>0){
                    Product oldProduct = old.get(column.getKey());
                    if(oldProduct!=null&& !categoryId.equals(oldProduct.getProductCategoryId()))
                        if(redisUtil.sSet(generateRedisCategoryKey(categoryId),newProduct.getId())<=0){
                            RedisRetryEvent rre = new RedisRetryEvent.Builder()
                                    .key(generateRedisCategoryKey(categoryId))
                                    .value(String.valueOf(newProduct.getId()))
                                    .commandType(RedisConstant.CommandType.SET_SET)
                                    .build();
                            rocketMQTemplate.asyncSend(redisRetryTopic, MessageBuilder.withPayload(rre).build(), new SendCallback() {
                                @Override
                                public void onSuccess(SendResult sendResult) {
                                }

                                @Override
                                public void onException(Throwable throwable) {
                                    if(redisUtil.sSet(generateRedisCategoryKey(categoryId),newProduct.getId())<=0){
                                        LOGGER.error("send set_set redis topic fail; {}", throwable.getMessage());
                                    }
                                }
                            },rocketMQTemplate.getProducer().getSendMsgTimeout(),DELAY_LEVEL);
                        }
                }
            }
        }
    }


