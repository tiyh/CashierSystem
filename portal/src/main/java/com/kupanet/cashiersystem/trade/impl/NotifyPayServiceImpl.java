package com.kupanet.cashiersystem.trade.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kupanet.cashiersystem.constant.PayConstant;
import com.kupanet.cashiersystem.model.Order;
import com.kupanet.cashiersystem.model.OrderPaidEvent;
import com.kupanet.cashiersystem.service.order.OrderService;
import com.kupanet.cashiersystem.trade.NotifyPayService;
import com.kupanet.cashiersystem.web.PayController;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@PropertySource({"classpath:zfbinfo.properties"})
public class NotifyPayServiceImpl implements NotifyPayService {
    private static Logger LOGGER = LoggerFactory.getLogger(NotifyPayServiceImpl.class);


    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${sign_type}")
    private String signType;
    @Value("${public_key}")
    private String publicKey;

    @Value("${cashier.rocketmq.orderTopic}")
    private String orderTopic;
    private final static ObjectMapper mapper = new ObjectMapper();


    @Autowired
    private OrderService orderService;
    @Override
    public String handleAliPayNotify(Map params)  {
        boolean  passSign = false;
        try{
            passSign = AlipaySignature.rsaCheckV1(params, publicKey, "UTF-8", signType);
        }catch (AlipayApiException e){
            LOGGER.error("check sign fail,trade_no:"+params.get("trade_no")+"_"+e.getMessage(),e);
            return PayConstant.AlipayReturnEnum.FAILED.getName();;
        }
        if(!passSign){
            LOGGER.warn("check sign fail,trade_no:"+params.get("trade_no"));
            return PayConstant.AlipayReturnEnum.FAILED.getName();
        }
        String trade_status = String.valueOf(params.get("trade_status"));	// 交易状态
        String trade_no = String.valueOf(params.get("trade_no"));			// 渠道订单号
        Long orderId=0L;
        try{
            orderId = Long.parseLong(trade_no);
        }catch (NumberFormatException e){
            LOGGER.error("trade_no:"+trade_no+"parseLong_"+e.getMessage(),e);
            return PayConstant.RETURN_ALIPAY_VALUE_FAIL;
        }
        Order order = orderService.getOrderById(orderId);
        int payStatus = order.getStatus();
        if (trade_status.equals(PayConstant.PayStatus.SUCCESS.getString()) ||
                trade_status.equals(PayConstant.PayStatus.FINISHED.getString())) {
            if (payStatus != PayConstant.PayStatus.SUCCESS.getInt()
                    && payStatus != PayConstant.PayStatus.FINISHED.getInt()) {
                int newStatus = PayConstant.PayStatus.getInt(trade_status);
                orderService.updateNote(orderId,trade_no+",alipay:"+trade_status, newStatus);
            }
        }else{
            LOGGER.warn("trade_no:{},trade_status:{}",trade_no,trade_status);
            return PayConstant.AlipayReturnEnum.FAILED.getName();
        }
        doNotify(order);
        return PayConstant.AlipayReturnEnum.SUCCESS.getName();
    }



    public void doNotify(Order order) {
        SendResult sendResult = rocketMQTemplate.syncSend(orderTopic, new OrderPaidEvent(order.getId(),order.getPayAmount(),
                createNotifyUrl(order),System.currentTimeMillis()));
        LOGGER.info("syncSend1 to topic:{} sendResult={}", orderTopic, sendResult.toString());

    }
    public String createNotifyUrl(Order payOrder) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", String.valueOf(payOrder.getId()));           // 支付订单号
        paramMap.put("amount", payOrder.getPayAmount() == null ? "" : payOrder.getPayAmount());                      	// 支付金额
        paramMap.put("status", payOrder.getStatus() == null ? "" : payOrder.getStatus());               		// 支付状态
        paramMap.put("paymentTime", payOrder.getPaymentTime()==null ? "" : payOrder.getPaymentTime());     	   				// 商品标题
        paramMap.put("payTime", payOrder.getPayType()==null ? "" : payOrder.getPayType());               		   	// 扩展参数1
        //todo sign
        String param = genUrlParams(paramMap);
        StringBuffer sb = new StringBuffer();
        sb.append(payOrder.getNotifyUrl()).append("?").append(param);
        return sb.toString();
    }

    public static String genUrlParams(Map<String, Object> paraMap) {
        if(paraMap == null || paraMap.isEmpty()) return "";
        StringBuffer urlParam = new StringBuffer();
        Set<String> keySet = paraMap.keySet();
        int i = 0;
        for(String key:keySet) {
            urlParam.append(key).append("=").append(paraMap.get(key));
            if(++i == keySet.size()) break;
            urlParam.append("&");
        }
        return urlParam.toString();
    }
}
