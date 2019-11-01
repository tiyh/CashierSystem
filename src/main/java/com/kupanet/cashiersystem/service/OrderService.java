package com.kupanet.cashiersystem.service;

import java.util.List;

import com.kupanet.cashiersystem.model.Order;

public interface OrderService {
	Order queryById(int id);
	List<Order> queryByNumbers(String numbers);
	List<Order> queryLastByNumbers(String numbers);
	int countNextByNumbers(String numbers);
	List<Order> queryNextByNumbers(String numbers);
}
