package com.kupanet.cashiersystem.service.mq;

import com.kupanet.cashiersystem.model.BloomRetryEvent;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@RocketMQMessageListener(topic = "${cashier.rocketmq.redis.bloomTopic}", consumerGroup = "redis-bloom-consumer")

public class RedisBloomRetryConsumer implements RocketMQListener<BloomRetryEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(RedisBloomRetryConsumer.class);

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void onMessage(BloomRetryEvent event) {
        if(!redisUtil.addBloom(event.getKey(),event.getValue())){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!redisUtil.addBloom(event.getKey(),event.getValue())) {
                LOGGER.error("redis addBloom key:{},value:{} failed", event.getKey(), event.getValue());
            }
        }
    }
}
