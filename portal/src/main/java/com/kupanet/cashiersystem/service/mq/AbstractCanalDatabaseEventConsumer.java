package com.kupanet.cashiersystem.service.mq;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kupanet.cashiersystem.constant.SQLType;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public abstract class AbstractCanalDatabaseEventConsumer<T> {
    @Autowired
    private RedisUtil redisUtil;
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCanalDatabaseEventConsumer.class);

    public final static ObjectMapper mapper = new ObjectMapper();

    public void process(FlatMessage flatMessage) {
        //By default, Consume_Success is returned. If an exception is thrown in onMessage method, RECONSUME_LATER will be returned. So if you want resend a message, just throw exception.
        LOGGER.error("onMessage type:{}",flatMessage.getType());
        long batchId = flatMessage.getId();
        if (batchId == -1 ) {
            LOGGER.error("onMessage batchId:{}",batchId);
        } else {
            Set<T> data = getData(flatMessage);
            switch (flatMessage.getType()){
                case SQLType.INSERT:redisInsert(data);break;
                case SQLType.UPDATE:redisUpdate(data);break;
                case SQLType.DELETE:redisDelete(data);break;
                default:break;
            }
        }
    }

    private void redisInsert( Collection<T> columns){
        Random r = new Random();
        int randomInt = 120+r.nextInt(60);
        for (T column : columns) {
            LOGGER.error("redisInsert getWrapRedisKey:{},column:{}",getWrapRedisKey(column),column);
            redisUtil.set(getWrapRedisKey(column),column,randomInt);
            //todo
            redisUtil.addBloom(getModelName(),String.valueOf(getIdValue(column)));
        }
    }

    private void redisUpdate( Collection<T> columns){
        Random r = new Random();
        int randomInt = 120+r.nextInt(60);
        for (T column : columns) {
            LOGGER.error("redisUpdate getWrapRedisKey:{},column:{}",getWrapRedisKey(column),column);
            redisUtil.set(getWrapRedisKey(column),column,randomInt);
        }
    }

    private void redisDelete( Collection<T> columns){
        for (T column : columns) {
            LOGGER.error("redisDelete getWrapRedisKey:{},column:{}",getWrapRedisKey(column),column);
            redisUtil.del(getWrapRedisKey(column));
        }

    }
    protected Set<T> getData(FlatMessage flatMessage) {
        List<Map<String, String>> sourceData = flatMessage.getData();
        Set<T> targetData = new HashSet(sourceData.size());
        for (Map<String, String> map : sourceData) {
            T t =mapper.convertValue(map,getClassType());
            targetData.add(t);
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
    protected abstract String getModelName();
    protected abstract String getIdValue(T t);


}
