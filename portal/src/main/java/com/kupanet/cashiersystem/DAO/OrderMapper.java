package com.kupanet.cashiersystem.DAO;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import com.kupanet.cashiersystem.model.Order;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
	/**
	 * 批量修改订单状态
	 */
	int updateOrderStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

}
