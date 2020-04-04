package com.kupanet.cashiersystem.service.mq;

import com.kupanet.cashiersystem.util.RedisUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@RocketMQMessageListener(topic = "${cashier.rocketmq.redis.deleteTopic}", consumerGroup = "redis-delete-consumer")

public class RedisDeleteRetryConsumer implements RocketMQListener<String> {

    private static Logger LOGGER = LoggerFactory.getLogger(RedisDeleteRetryConsumer.class);

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void onMessage(String key) {
        if(!redisUtil.del(key)){
            LOGGER.warn("redis delete {} failed",key);
        }
    }

}
