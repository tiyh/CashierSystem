package com.kupanet.cashiersystem.web;

import com.kupanet.cashiersystem.service.order.OrderService;
import com.kupanet.cashiersystem.trade.TradeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/pay")
public class PayController {
    private static Logger LOGGER = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private TradeService tradeService;

    @GetMapping("qr")
    public Object tradePreCreate(@RequestParam(value = "id", required = false, defaultValue = "0") Long id){
        return tradeService.tradePreCreate(id);
    }
    @GetMapping("query")
    public Object tradeQuery(@RequestParam(value = "tradeNo", required = true) String tradeNo){
        return tradeService.tradeQuery(tradeNo);
    }

    @PostMapping("refund")
    public Object refund(Long orderId,String reason) {
        return tradeService.tradeRefund(orderId,reason);
    }

}
