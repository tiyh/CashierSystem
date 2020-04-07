package com.kupanet.cashiersystem.util;

import io.rebloom.client.Client;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisTemplatePlus<K, V>  extends RedisTemplate<K, V>  {
    private Client client;
    public RedisTemplatePlus(String host,int port){
        super();
        client= new Client(host, port);
    }
    public void createBloomFilter(String name, long initCapacity, double errorRate) {
        if(client==null)
            client.createFilter(name,initCapacity,errorRate);
    }
    public boolean addBloom(String name, String value) {
        if(client==null||name==null||value==null) return false;
        return client.add(name,value);
    }
    public boolean[] addBloomMulti(String name, String ...values) {
        if(client==null||name==null||values==null) return new boolean[values.length];
        return client.addMulti(name,values);
    }
    public boolean existsBloom(String name, String value) {
        if(client==null||name==null||value==null) return false;
        return client.exists(name, value);
    }
}
