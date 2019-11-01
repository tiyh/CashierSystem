package com.kupanet.cashiersystem.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kupanet.cashiersystem.model.Order;

@Mapper
public interface OrderMapper {
	Order queryById(int id);
	List<Order> queryByNumbers(String numbers);
	List<Order> queryLastByNumbers(String numbers);
	int countNextByNumbers(String numbers);
	List<Order> queryNextByNumbers(String numbers);
}
