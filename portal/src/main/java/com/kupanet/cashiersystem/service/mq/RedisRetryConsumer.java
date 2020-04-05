package com.kupanet.cashiersystem.service.mq;

import com.kupanet.cashiersystem.model.RedisRetryEvent;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@RocketMQMessageListener(topic = "${cashier.rocketmq.redis.retryTopic}", consumerGroup = "redis-retry-consumer")
public class RedisRetryConsumer implements RocketMQListener<RedisRetryEvent> {
    private static Logger LOGGER = LoggerFactory.getLogger(RedisRetryConsumer.class);

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void onMessage(RedisRetryEvent redisRetryEvent) {
        switch (redisRetryEvent.getCommandType()){
            case DELETE:
                String key = redisRetryEvent.getKey();
                if(key!=null&& !key.isEmpty()){
                    if(!redisUtil.del(key)){
                        LOGGER.warn("redis delete {} failed",key);
                    }
                }
                break;
            case BlOOM_ADD:
                if(!redisUtil.addBloom(redisRetryEvent.getKey(),redisRetryEvent.getValue())){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!redisUtil.addBloom(redisRetryEvent.getKey(),redisRetryEvent.getValue())) {
                        LOGGER.error("redis addBloom key:{},value:{} failed", redisRetryEvent.getKey(), redisRetryEvent.getValue());
                    }
                }
                break;
            case SET_SET:
                if(redisUtil.sSet(redisRetryEvent.getKey(),redisRetryEvent.getValue())<=0){
                    LOGGER.error("redis sSet key:{},value:{} failed", redisRetryEvent.getKey(), redisRetryEvent.getValue());

                }
                break;
            default:break;
        }
    }
}
