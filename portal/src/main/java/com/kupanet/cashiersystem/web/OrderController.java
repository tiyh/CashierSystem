package com.kupanet.cashiersystem.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kupanet.cashiersystem.model.MoneyInfoParam;
import com.kupanet.cashiersystem.model.Order;
import com.kupanet.cashiersystem.model.OrderItem;
import com.kupanet.cashiersystem.service.order.OrderItemService;
import com.kupanet.cashiersystem.service.order.OrderService;
import com.kupanet.cashiersystem.util.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单表
 *
 */
@Slf4j
@RestController
public class OrderController {
	private final Logger logger = LoggerFactory.getLogger(OrderController.class);
	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderItemService orderItemService;

	@GetMapping(value = "/order/all")
	public Object getOrderByPage(Order order,
								 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
								 @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize
	) {
		try {
			List<Order> orderList = orderService.list(new QueryWrapper<>(order));
			for (Order mOrder : orderList){
				OrderItem orderItem = new OrderItem();
				orderItem.setOrderId(mOrder.getId());
				List<OrderItem> orderItemList = orderItemService.list(new QueryWrapper<>(orderItem));
				mOrder.setOrderItemList(orderItemList);
			}
			return new CommonResult().success(orderList);
		}catch (Exception e) {
			logger.error("根据条件查询所有订单表列表：%s", e.getMessage(), e);
		}
		return new CommonResult().failed();

	}
	@GetMapping(value = "/order/{id}")
	public Object getOrderById( @PathVariable Long id) {
		try {
			if (id==null||id==0) {
				return new CommonResult().paramFailed("订单表id");
			}
			Order mOrder = orderService.getOrderById(id);
			return new CommonResult().success(mOrder);
		} catch (Exception e) {
			logger.error("查询订单表明细：%s", e.getMessage(), e);
			return new CommonResult().failed();
		}

	}
	@DeleteMapping(value = "/order/{id}")
	public Object deleteOrder(@PathVariable Long id) {
		try {
			if (id==null||id==0) {
				return new CommonResult().paramFailed("订单表id");
			}
			if (orderService.removeById(id)) {
				return new CommonResult().success();
			}
		} catch (Exception e) {
			logger.error("删除订单表：%s", e.getMessage(), e);
			return new CommonResult().failed();
		}
		return new CommonResult().failed();
	}


	@PostMapping(value = "/order")
	public Object createOrder(@RequestBody Order entity) {
		try {
			String orderId = orderService.createOrder(entity);
			return new CommonResult().success(orderId);
		} catch (Exception e) {
			logger.error("保存订单表：%s", e.getMessage(), e);
		}
		return new CommonResult().failed();
	}
	@PostMapping(value = "/order/cart")
	public Object createOrderFromCart(String cartIds, int payType, String notifyUrl, boolean all) {
		try {
			return orderService.createOrderFromCart(cartIds,payType,notifyUrl,all);
		} catch (Exception e) {
			logger.error("保存订单表：%s", e.getMessage(), e);
		}
		return new CommonResult().failed();
	}


	@PutMapping(value = "/order")
	public Object updateOrder(@RequestBody Order entity) {
		try {
			if (orderService.updateById(entity)) {
				return new CommonResult().success();
			}
		} catch (Exception e) {
			logger.error("更新订单表：%s", e.getMessage(), e);
			return new CommonResult().failed();
		}
		return new CommonResult().failed();
	}

	@DeleteMapping (value = "/order/batch")
	public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
		boolean count = orderService.removeByIds(ids);
		if (count) {
			return new CommonResult().success(count);
		} else {
			return new CommonResult().failed();
		}
	}


	@PutMapping(value = "/order/batch")
	@ResponseBody
	public Object close(@RequestParam("ids") List<Long> ids, @RequestParam String note) {
		int count = orderService.close(ids, note);
		if (count > 0) {
			return new CommonResult().success(count);
		}
		return new CommonResult().failed();
	}



	@PutMapping (value = "/order/moneyInfo")
	public Object updateReceiverInfo(@RequestBody MoneyInfoParam moneyInfoParam) {
		int count = orderService.updateMoneyInfo(moneyInfoParam);
		if (count > 0) {
			return new CommonResult().success(count);
		}
		return new CommonResult().failed();
	}

	@PutMapping(value = "/order/note")
	@ResponseBody
	public Object updateNote(@RequestParam("id") Long id,
							 @RequestParam("note") String note,
							 @RequestParam("status") Integer status) {
		int count = orderService.updateNote(id, note, status);
		if (count > 0) {
			return new CommonResult().success(count);
		}
		return new CommonResult().failed();
	}
}
