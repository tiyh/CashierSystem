package com.kupanet.cashiersystem.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单表
 */
//@Data
@Getter
@Setter
@TableName("cs_order")
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableField(exist = false)
	List<OrderItem> orderItemList;
	/**
	 * 订单id
	 */
	@TableId(value = "id", type = IdType.INPUT)
	private Long id;

	@TableField("member_id")
	private Long memberId;

	/**
	 * 订单编号
	 */
	@TableField("order_sn")
	private String orderSn;

	/**
	 * 提交时间
	 */
	@TableField("create_time")
	private Date createTime;

	/**
	 * 用户帐号
	 */
	@TableField("member_username")
	private String memberUsername;

	/**
	 * 订单总金额
	 */
	@TableField("total_amount")
	private BigDecimal totalAmount;

	/**
	 * 应付金额（实际支付金额）
	 */
	@TableField("pay_amount")
	private BigDecimal payAmount;


	/**
	 * 管理员后台调整订单使用的折扣金额
	 */
	@TableField("discount_amount")
	private BigDecimal discountAmount;

	/**
	 * 支付方式：0->未支付；1->支付宝；2->微信
	 */
	@TableField("pay_type")
	private Integer payType;


	/**
	 * 订单状态：0->待付款；1->已完成；2->已关闭；3->无效订单
	 */
	private Integer status;


	/**
	 * 发票类型：0->不开发票；1->电子发票；2->纸质发票
	 */
	@TableField("bill_type")
	private Integer billType;

	/**
	 * 发票抬头
	 */
	@TableField("bill_header")
	private String billHeader;

	/**
	 * 发票内容
	 */
	@TableField("bill_content")
	private String billContent;

	/**
	 * 收货人姓名
	 */
	@TableField("receiver_name")
	private String receiverName;

	/**
	 * 收货人电话
	 */
	@TableField("receiver_phone")
	private String receiverPhone;

	/**
	 * 订单备注
	 */
	private String note;


	/**
	 * 支付时间
	 */
	@TableField("payment_time")
	private Date paymentTime;

	/**
	 * 修改时间
	 */
	@TableField("modify_time")
	private Date modifyTime;
	
	@TableField("promotion_amount")
	private BigDecimal promotionAmount;

	public Order(){

	}
}

