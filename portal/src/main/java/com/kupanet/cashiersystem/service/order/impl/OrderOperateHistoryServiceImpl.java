package com.kupanet.cashiersystem.service.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kupanet.cashiersystem.DAO.OrderOperateHistoryMapper;
import com.kupanet.cashiersystem.model.OrderOperateHistory;
import com.kupanet.cashiersystem.service.order.OrderOperateHistoryService;
import org.springframework.stereotype.Service;

@Service
public class OrderOperateHistoryServiceImpl
        extends ServiceImpl<OrderOperateHistoryMapper, OrderOperateHistory>
        implements OrderOperateHistoryService {
}
