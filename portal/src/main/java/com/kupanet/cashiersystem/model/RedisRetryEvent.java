package com.kupanet.cashiersystem.model;


import com.kupanet.cashiersystem.constant.RedisConstant;

import java.io.Serializable;

public class RedisRetryEvent implements Serializable {
    private static final long serialVersionUID = 5479875081384333294L;

    private String key;

    private String value;

    private RedisConstant.CommandType commandType;

    public RedisRetryEvent() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RedisConstant.CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(RedisConstant.CommandType commandType) {
        this.commandType = commandType;
    }

    RedisRetryEvent(Builder builder) {
        this.key = builder.key;
        this.value = builder.value;
    }


    public static final class Builder {
        String key;
        String value;
        RedisConstant.CommandType commandType;

        public Builder key(String key) {
            this.key = key;
            return this;
        }
        public Builder value(String value) {
            this.value = value;
            return this;
        }
        public Builder commandType(RedisConstant.CommandType commandType){
            this.commandType=commandType;
            return this;
        }


        public RedisRetryEvent build() {
            return new RedisRetryEvent(this);
        }
    }




}

