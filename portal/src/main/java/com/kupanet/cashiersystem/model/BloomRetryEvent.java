package com.kupanet.cashiersystem.model;

import java.math.BigDecimal;

public class BloomRetryEvent {
    private static final long serialVersionUID = 1L;

    private String key;

    private String value;

    public BloomRetryEvent() {
    }

    public BloomRetryEvent(String key, String value) {
        this.key = key;
        this.value = value;
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




}
