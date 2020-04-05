package com.kupanet.cashiersystem.service.mq;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.kupanet.cashiersystem.constant.SQLType;
import com.kupanet.cashiersystem.model.BloomRetryEvent;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;

import java.lang.reflect.ParameterizedType;
import java.util.*;

public abstract class AbstractCanalDatabaseEventConsumer<T> {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCanalDatabaseEventConsumer.class);

    @Value("${cashier.rocketmq.redis.deleteTopic}")
    protected String deleteTopic;

    @Value("${cashier.rocketmq.redis.bloomTopic}")
    protected String bloomTopic;

    @Autowired
    protected RedisUtil redisUtil;

    @Autowired
    protected RocketMQTemplate rocketMQTemplate;
    //messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
    protected final static int DELAY_LEVEL = 1;

    public final static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }
    public void process(FlatMessage flatMessage) {
        //By default, Consume_Success is returned. If an exception is thrown in onMessage method, RECONSUME_LATER will be returned. So if you want resend a message, just throw exception.
        LOGGER.info("onMessage type:{}",flatMessage.getType());
        long batchId = flatMessage.getId();
        if (batchId == -1 ) {
            LOGGER.error("onMessage batchId:{}",batchId);
        } else {
            switch (flatMessage.getType()){
                case SQLType.INSERT:redisInsert(getData(flatMessage));break;
                case SQLType.DELETE:redisDelete(getData(flatMessage));break;
                case SQLType.CACHE_FROM_QUERY:
                case SQLType.UPDATE:redisUpdate(getData(flatMessage),getOldData(flatMessage));break;
                default:{
                    LOGGER.warn("onMessage :{} do nothing",flatMessage.getType());
                    break;
                }
            }
        }
    }



    protected Map<String,T> getData(FlatMessage flatMessage) {
        List<Map<String, String>> sourceData = flatMessage.getData();
        Map<String,T> targetData = new HashMap<>(sourceData.size());
        for (Map<String, String> map : sourceData) {
            String jsonStr = null;
            try {
                jsonStr = mapper.writeValueAsString(map);
                LOGGER.info("onMessage jsonStr:{}",jsonStr);
                T t =mapper.readValue(jsonStr,getClassType());
                targetData.put(jsonStr,t);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return targetData;
    }
    protected Map<String,T> getOldData(FlatMessage flatMessage) {
        List<Map<String, String>> sourceData = flatMessage.getOld();
        Map<String,T> targetData = new HashMap<>(sourceData.size());
        for (Map<String, String> map : sourceData) {
            String jsonStr = null;
            try {
                jsonStr = mapper.writeValueAsString(map);
                LOGGER.info("onMessage jsonStr:{}",jsonStr);
                T t =mapper.readValue(jsonStr,getClassType());
                targetData.put(jsonStr,t);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return targetData;
    }
    protected Class<T> getClassType() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    protected void redisInsert( Map<String,T> columns){
        Random r = new Random();
        int randomInt = 120+r.nextInt(60);
        for (Map.Entry<String,T> column : columns.entrySet()) {
            LOGGER.info("redisInsert getWrapRedisKey:{},column:{}",getWrapRedisKey(column.getValue()),column.getKey());
            redisUtil.set(getWrapRedisKey(column.getValue()),column.getKey(),randomInt);
            if(!redisUtil.addBloom(getModelName(),String.valueOf(getIdValue(column.getValue())))){
                rocketMQTemplate.asyncSend(bloomTopic, MessageBuilder.withPayload(new BloomRetryEvent(getModelName(),String.valueOf(getIdValue(column.getValue())))).build(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        if(!redisUtil.addBloom(getModelName(),String.valueOf(getIdValue(column.getValue())))){
                            LOGGER.error("send delete redis topic fail; {}", throwable.getMessage());
                        }
                    }
                },rocketMQTemplate.getProducer().getSendMsgTimeout(),DELAY_LEVEL);
            }

        }
    }
    protected void redisUpdate( Map<String,T> columns,Map<String,T> old){
        Random r = new Random();
        int randomInt = 120+r.nextInt(60);
        for (Map.Entry<String,T> column : columns.entrySet()) {
            LOGGER.info("redisUpdate getWrapRedisKey:{},column:{}",getWrapRedisKey(column.getValue()),column.getKey());
            redisUtil.set(getWrapRedisKey(column.getValue()),column.getKey(),randomInt);
        }
    }
    protected void redisDelete( Map<String,T> columns){
        for (Map.Entry<String,T> column : columns.entrySet()) {
            LOGGER.info("redisDelete getWrapRedisKey:{},column:{}",getWrapRedisKey(column.getValue()),column.getKey());
            if(!redisUtil.del(getWrapRedisKey(column.getValue()))){
                String message = getWrapRedisKey(column.getValue());
                rocketMQTemplate.asyncSend(deleteTopic, MessageBuilder.withPayload(message).build(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        if(!redisUtil.del(getWrapRedisKey(column.getValue()))){
                            LOGGER.error("send delete redis topic fail; {}", throwable.getMessage());
                        }
                    }
                },rocketMQTemplate.getProducer().getSendMsgTimeout(),DELAY_LEVEL);
            }
        }

    }
    protected abstract String getModelName();
    protected abstract String getIdValue(T t);
    protected abstract String getWrapRedisKey(T t);

}
