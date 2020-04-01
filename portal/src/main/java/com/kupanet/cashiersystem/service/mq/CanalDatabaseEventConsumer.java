package com.kupanet.cashiersystem.service.mq;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.kupanet.cashiersystem.model.Product;
import com.kupanet.cashiersystem.trade.impl.NotifyPayServiceImpl;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
        return "cs_product";
    }

    @Override
    protected String getIdValue(Product product) {
        return String.valueOf(product.getId());
    }
}

