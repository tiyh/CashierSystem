package com.kupanet.cashiersystem.service.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kupanet.cashiersystem.DAO.OrderItemMapper;
import com.kupanet.cashiersystem.model.OrderItem;
import com.kupanet.cashiersystem.service.order.OrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl
        extends ServiceImpl<OrderItemMapper, OrderItem>
        implements OrderItemService {
}
