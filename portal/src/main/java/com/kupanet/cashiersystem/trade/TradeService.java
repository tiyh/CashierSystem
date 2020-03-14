package com.kupanet.cashiersystem.trade;

import com.kupanet.cashiersystem.util.CommonResult;
public interface TradeService {
     // 当面付2.0查询订单
     CommonResult tradeQuery(Long orderId);
     // 当面付2.0退款
     CommonResult tradeRefund(Long id,String storeId,String reason);
     // 当面付2.0生成支付二维码
     CommonResult tradePreCreate(Long orderId,String operatorId,String storeId) ;

}

