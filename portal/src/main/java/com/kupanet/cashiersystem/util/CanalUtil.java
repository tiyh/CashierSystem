package com.kupanet.cashiersystem.util;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kupanet.cashiersystem.constant.SQLType;
import java.util.*;

public class CanalUtil<T> {
    public final static ObjectMapper mapper = new ObjectMapper();
    public FlatMessage convertFlatMessageFromObject(T t){
        FlatMessage flatMessage = new FlatMessage();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> data = new LinkedList<>();
        data.add(objectMapper.convertValue(t, Map.class));
        flatMessage.setData(data);
        //flatMessage.setTable(table);
        flatMessage.setType(SQLType.CACHE_FROM_QUERY);
        return flatMessage;
    }
    public FlatMessage convertFlatMessageFromObjects(List<T> ts){
        FlatMessage flatMessage = new FlatMessage();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> data = new LinkedList<>();
        for(T t:ts){
            data.add(objectMapper.convertValue(t, Map.class));
        }
        flatMessage.setData(data);
        //flatMessage.setTable(table);
        flatMessage.setType(SQLType.CACHE_FROM_QUERY);
        return flatMessage;
    }
}
