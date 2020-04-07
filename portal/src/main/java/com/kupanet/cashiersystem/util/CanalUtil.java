package com.kupanet.cashiersystem.util;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.kupanet.cashiersystem.constant.SQLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CanalUtil<T> {
    private final Logger logger = LoggerFactory.getLogger(CanalUtil.class);
    public FlatMessage convertFlatMessageFromObject(T t){
        FlatMessage flatMessage = new FlatMessage();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> data = new LinkedList<>();
        objectMapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS,true);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        try {
            String json = objectMapper.writeValueAsString(t);
            data.add(objectMapper.readValue(json,Map.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        flatMessage.setData(data);
        //flatMessage.setTable(table);
        flatMessage.setIsDdl(false);
        flatMessage.setType(SQLType.CACHE_FROM_QUERY);
        return flatMessage;
    }
    public FlatMessage convertFlatMessageFromObjects(List<T> ts){
        FlatMessage flatMessage = new FlatMessage();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS,true);
        objectMapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS,true);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        List<Map<String, String>> data = new LinkedList<>();
        for(T t:ts){
            try {
                String json = objectMapper.writeValueAsString(t);
                data.add(objectMapper.readValue(json,Map.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        flatMessage.setData(data);
        //flatMessage.setTable(table);
        flatMessage.setIsDdl(false);
        flatMessage.setType(SQLType.CACHE_FROM_QUERY);
        return flatMessage;
    }
}
