package com.kupanet.cashiersystem.service.order.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kupanet.cashiersystem.DAO.OrderMapper;
import com.kupanet.cashiersystem.DAO.OrderOperateHistoryMapper;
import com.kupanet.cashiersystem.model.MoneyInfoParam;
import com.kupanet.cashiersystem.model.Order;
import com.kupanet.cashiersystem.model.OrderOperateHistory;
import com.kupanet.cashiersystem.service.order.OrderOperateHistoryService;
import com.kupanet.cashiersystem.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderOperateHistoryMapper orderOperateHistoryMapper;

    private OrderOperateHistoryService orderOperateHistoryService;

    @Override
    public int close(List<Long> ids, String note) {
        Order record = new Order();
        record.setStatus(4);
        int count = orderMapper.update(record, new QueryWrapper<Order>().eq("delete_status",0).in("id",ids));
        List<OrderOperateHistory> historyList = ids.stream().map(orderId -> {
            OrderOperateHistory history = new OrderOperateHistory();
            history.setOrderId(orderId);
            history.setCreateTime(new Date());
            history.setOperateMan("后台管理员");
            history.setOrderStatus(4);
            history.setNote("订单关闭:" + note);
            return history;
        }).collect(Collectors.toList());
        orderOperateHistoryService.saveBatch(historyList);
        return count;
    }

    @Override
    public int updateMoneyInfo(MoneyInfoParam moneyInfoParam) {
        return 0;
    }

    @Override
    public int updateNote(Long id, String note, Integer status) {
        Order order = new Order();
        order.setId(id);
        order.setNote(note);
        order.setModifyTime(new Date());
        int count = orderMapper.updateById(order);
        OrderOperateHistory history = new OrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(status);
        history.setNote("修改备注信息：" + note);
        orderOperateHistoryMapper.insert(history);
        return count;
    }
}
