package com.kupanet.cashiersystem.service.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kupanet.cashiersystem.constant.PayConstant;
import com.kupanet.cashiersystem.model.OrderPaidEvent;
import com.kupanet.cashiersystem.service.order.OrderService;
import com.kupanet.cashiersystem.trade.impl.NotifyPayServiceImpl;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Service
@RocketMQMessageListener(topic = "${cashier.rocketmq.orderTopic}", consumerGroup = "order-paid-consumer")
public class OrderPaidEventConsumer implements RocketMQListener<OrderPaidEvent> {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderService orderService;
    private static Logger LOGGER = LoggerFactory.getLogger(NotifyPayServiceImpl.class);

    @Override
    public void onMessage(OrderPaidEvent orderPaidEvent) {

        System.out.printf("------- OrderPaidEventConsumer received: %s [orderId : %s]\n", orderPaidEvent,orderPaidEvent.getOrderId());

            String respUrl = orderPaidEvent.getNotifyUrl();
            Long orderId = orderPaidEvent.getOrderId();
            if(respUrl==null||respUrl.isEmpty()) {
                LOGGER.error( "respUrl empty");
                return;
            }
            try {
                String notifyResult = "";
                try {
                    URI uri = new URI(respUrl);
                    notifyResult = restTemplate.postForObject(uri, null, String.class);
                }catch (Exception e) {
                    LOGGER.error( "notify Exception:"+e);
                }
                LOGGER.info("notify notifyResult:{} , OrderID={}",notifyResult, orderId);
                if(notifyResult!=null&&notifyResult.trim().equalsIgnoreCase("success")){
                    try {
                        orderService.updateNote(orderId,"notify success", PayConstant.PayStatus.FINISHED.getInt());
                    } catch (Exception e) {
                        LOGGER.error("updateNote:"+e);
                    }
                }else {
                    LOGGER.error("notify fail");
                    // TODO
                }
            } catch(Exception e) {
                LOGGER.error("notify exception. url:{},e:{}", respUrl,e);
            }

    }
}
