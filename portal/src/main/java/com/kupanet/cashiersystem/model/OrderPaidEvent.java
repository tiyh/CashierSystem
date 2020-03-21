package com.kupanet.cashiersystem.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderPaidEvent implements Serializable {
    private static final long serialVersionUID = 1674225916629499297L;
    private Long orderId;

    private BigDecimal paidMoney;

    private String notifyUrl;

    private Long createTime;

    public OrderPaidEvent() {
    }

    public OrderPaidEvent(Long orderId, BigDecimal paidMoney,
                          String notifyUrl,Long createTime) {
        this.orderId = orderId;
        this.paidMoney = paidMoney;
        this.notifyUrl =  notifyUrl;
        this.createTime = createTime;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getPaidMoney() {
        return paidMoney;
    }

    public void setPaidMoney(BigDecimal paidMoney) {
        this.paidMoney = paidMoney;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }


}

