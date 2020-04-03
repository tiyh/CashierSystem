package com.kupanet.cashiersystem.service.mq;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.kupanet.cashiersystem.constant.SQLType;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public abstract class AbstractCanalDatabaseEventConsumer<T> {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCanalDatabaseEventConsumer.class);

    @Autowired
    private RedisUtil redisUtil;

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
                case SQLType.UPDATE:redisUpdate(getData(flatMessage));break;
                case SQLType.DELETE:redisDelete(getData(flatMessage));break;
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
    protected Class<T> getClassType() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    protected String getWrapRedisKey(T t) {
        return new StringBuilder()
                .append(getModelName())
                .append("_")
                .append(getIdValue(t))
                .toString();

    }
    protected void redisInsert( Map<String,T> columns){
        Random r = new Random();
        int randomInt = 120+r.nextInt(60);
        for (Map.Entry<String,T> column : columns.entrySet()) {
            LOGGER.info("redisInsert getWrapRedisKey:{},column:{}",getWrapRedisKey(column.getValue()),column.getKey());
            redisUtil.set(getWrapRedisKey(column.getValue()),column.getKey(),randomInt);
            //todo
            redisUtil.addBloom(getModelName(),String.valueOf(getIdValue(column.getValue())));
        }
    }
    protected void redisUpdate( Map<String,T> columns){
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
            redisUtil.del(getWrapRedisKey(column.getValue()));
        }

    }
    protected abstract String getModelName();
    protected abstract String getIdValue(T t);


}
