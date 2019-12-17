package com.kupanet.leaf.service.impl;

import com.kupanet.leaf.component.ZookeeperHelper;
import com.kupanet.leaf.service.LeafSnowFlakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class LeafSnowFlakeServiceImpl implements LeafSnowFlakeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeafSnowFlakeServiceImpl.class);
    //
    // 相对时间点 20191217 21:27
    private final long startPoint = 1576589244278L;

    private final long workerIdBits = 10L;
    private final long maxWorkerId = ~(-1L << workerIdBits);//最大能够分配的workerid =1023
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = ~(-1L << sequenceBits);
    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private static final Random RANDOM = new Random();
    //private volatile AtomicBoolean initFlag = new AtomicBoolean(false);


    ZookeeperHelper holder;
    @Autowired
    public void LeafSnowFlakeServiceImpl(ZookeeperHelper holder){
        this.holder=holder;
        LOGGER.info(" LeafSnowFlakeServiceImpl init!!!!");
        if(timeGen() <= startPoint){
            throw new IllegalArgumentException("not support startPoint gt currentTime");
        }
        boolean initFlag = holder.init();
        if (initFlag) {
            workerId = holder.getWorkerID();
            LOGGER.info("START SUCCESS USE ZK WORKERID-{}", workerId);
        } else {
            throw new IllegalArgumentException("ZookeeperHelper not init ok");
        }
        if(workerId < 0 || workerId > maxWorkerId){
            throw new IllegalArgumentException("workerID must gte 0 and lte 1023");
        }
    }
    @Override
    public Long getId() {
        //init();
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        return Long.valueOf(-1);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("wait interrupted");
                    return Long.valueOf(-2);
                }
            } else {
                return Long.valueOf(-3);
            }
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //seq 为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //如果是新的ms开始
            sequence = RANDOM.nextInt(100);
        }
        lastTimestamp = timestamp;
        long id = ((timestamp - startPoint) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
        return id;
    }
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
