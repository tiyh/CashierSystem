package com.kupanet.cashiersystem.web;

import com.kupanet.cashiersystem.service.order.OrderService;
import com.kupanet.cashiersystem.trade.TradeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/pay")
public class PayController {
    private static Logger LOGGER = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private TradeService tradeService;

    @GetMapping("qr")
    public Object tradePreCreate(@RequestParam Long id,String operatorId,String storeId){
        return tradeService.tradePreCreate(id, operatorId, storeId);
    }
    @GetMapping("query")
    public Object tradeQuery(@RequestParam(value = "tradeNo", required = true) Long tradeNo){
        return tradeService.tradeQuery(tradeNo);
    }

    @PostMapping("refund")
    public Object refund(Long orderId,String storeId,String reason) {
        return tradeService.tradeRefund(orderId,storeId,reason);
    }

    @RequestMapping("notify/alipay.htm")
    @ResponseBody
    public String aliPayNotifyRes(HttpServletRequest request) {
        //todo
        return "";

    }
}
