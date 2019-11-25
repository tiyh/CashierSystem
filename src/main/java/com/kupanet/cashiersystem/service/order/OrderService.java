package com.kupanet.cashiersystem.service.order;


import com.baomidou.mybatisplus.extension.service.IService;
import com.kupanet.cashiersystem.model.MoneyInfoParam;
import com.kupanet.cashiersystem.model.Order;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 */
public interface OrderService extends IService<Order> {

    /**
     * 修改订单费用信息
     * @param moneyInfoParam
     */
    int updateMoneyInfo(MoneyInfoParam moneyInfoParam);

    /**
     * 修改订单备注
     */
    int updateNote(Long id, String note, Integer status);

    /**
     * 批量关闭订单
     */
    int close(List<Long> ids, String note);
}

