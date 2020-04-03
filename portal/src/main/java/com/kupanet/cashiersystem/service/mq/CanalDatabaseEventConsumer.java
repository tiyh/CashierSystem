package com.kupanet.cashiersystem.service.mq;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.kupanet.cashiersystem.constant.RedisConstant;
import com.kupanet.cashiersystem.model.Product;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Receive asynchronously delivered messages orderly. one queue, one thread
 */
@Service
@RocketMQMessageListener(topic = "${cashier.rocketmq.canalDatabaseTopic}",
        consumerGroup = "canal-database-consumer",consumeMode = ConsumeMode.ORDERLY)
public class CanalDatabaseEventConsumer extends AbstractCanalDatabaseEventConsumer<Product> implements RocketMQListener<FlatMessage> {
    private static Logger LOGGER = LoggerFactory.getLogger(CanalDatabaseEventConsumer.class);

    @Override
    public void onMessage(FlatMessage flatMessage) {
        process(flatMessage);
    }

    protected String getModelName(){
        return RedisConstant.PRODUCT_TABLE_NAME;
    }

    @Override
    protected String getIdValue(Product product) {
        return String.valueOf(product.getId());
    }

}

