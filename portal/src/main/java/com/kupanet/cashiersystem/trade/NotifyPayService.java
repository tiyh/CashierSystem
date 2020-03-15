package com.kupanet.cashiersystem.trade;

import com.alipay.api.AlipayApiException;

import java.util.Map;

public interface NotifyPayService {
    String handleAliPayNotify(Map<String, String> params);
}
