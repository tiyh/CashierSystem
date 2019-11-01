package com.kupanet.cashiersystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kupanet.cashiersystem.DAO.OrderMapper;
import com.kupanet.cashiersystem.model.Order;
@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderMapper orderMapper;
	@Override
	public Order queryById(int id) {
		return orderMapper.queryById(id);
	}

	@Override
	public List<Order> queryByNumbers(String numbers) {
		return orderMapper.queryByNumbers(numbers);
	}

	@Override
	public List<Order> queryLastByNumbers(String numbers) {
		return orderMapper.queryLastByNumbers(numbers);
	}

	@Override
	public int countNextByNumbers(String numbers) {
		return orderMapper.countNextByNumbers(numbers);
	}
	@Override
	public List<Order> queryNextByNumbers(String numbers){
		return orderMapper.queryNextByNumbers(numbers);
	}
}
