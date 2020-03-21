package com.kupanet.cashiersystem.web;

import com.kupanet.cashiersystem.constant.PayConstant;
import com.kupanet.cashiersystem.service.order.OrderService;
import com.kupanet.cashiersystem.trade.NotifyPayService;
import com.kupanet.cashiersystem.trade.TradeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@RestController
@RequestMapping("/pay")
public class PayController {
    private static Logger LOGGER = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private NotifyPayService notifyPayService;

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
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        LOGGER.info("notify:reqStr="+params);
        if(params.isEmpty()) {
            LOGGER.warn("failed,params isEmpty,not handleAliPayNotify");
            return PayConstant.AlipayReturnEnum.FAILED.getName();
        }
        return notifyPayService.handleAliPayNotify(params);

    }
}
