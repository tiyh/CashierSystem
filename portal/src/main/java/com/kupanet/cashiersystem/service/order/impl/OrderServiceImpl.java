package com.kupanet.cashiersystem.service.order.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kupanet.cashiersystem.DAO.OrderItemMapper;
import com.kupanet.cashiersystem.DAO.OrderMapper;
import com.kupanet.cashiersystem.DAO.OrderOperateHistoryMapper;
import com.kupanet.cashiersystem.constant.PayConstant;
import com.kupanet.cashiersystem.model.*;
import com.kupanet.cashiersystem.service.IDGeneratorService;
import com.kupanet.cashiersystem.service.cart.CartService;
import com.kupanet.cashiersystem.service.order.OrderItemService;
import com.kupanet.cashiersystem.service.order.OrderOperateHistoryService;
import com.kupanet.cashiersystem.service.order.OrderService;
import com.kupanet.cashiersystem.service.product.ProductService;
import com.kupanet.cashiersystem.util.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
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

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderOperateHistoryService orderOperateHistoryService;

    @Autowired
    private IDGeneratorService idGeneratorService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    private Long getMemberId(){
        return 1L;
    }

    @Override
    public Order getOrderById(Long id){
        Order mOrder = getById(id);
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(mOrder.getId());
        List<OrderItem> orderItemList = orderItemService.list(new QueryWrapper<>(orderItem));
        mOrder.setOrderItemList(orderItemList);
        return mOrder;
    }


    @Override
    @Transactional
    public int close(List<Long> ids, String note) {
        Order record = new Order();
        record.setStatus(PayConstant.PayStatus.FAILED.getInt());
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
    @Transactional
    public int updateNote(Long id, String note, Integer status) {
        Order order = new Order();
        order.setId(id);
        order.setNote(note);
        order.setModifyTime(new Date());
        order.setStatus(status);
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
    @Override
    public String createOrder(Order order) {
        String orderSn = idGeneratorService.getId();
        Long orderId =  Long.parseLong(orderSn);
        order.setOrderSn(orderSn);
        order.setId(orderId);
        order.setCreateTime(new Date());
        //向订单明细表插入数据。
        List<OrderItem> orderItems = order.getOrderItemList();
        for (OrderItem orderItem : orderItems) {
            orderItem.setId(Long.parseLong(idGeneratorService.getId()));
            orderItem.setOrderId(orderId);
            orderItemMapper.insert(orderItem);
        }
        save(order);
        return orderSn;
    }

    @Transactional
    public CommonResult createOrderFromCart(String cartIds,int payType,String notifyUrl,boolean all) {
        String orderSn = idGeneratorService.getId();
        Long orderId =  Long.parseLong(orderSn);
        List<CartItem> cartItemList =new ArrayList<>();
        if (all){
            cartItemList = cartService.list(getMemberId());
        } else{
            String[] ids =cartIds.split(",");
            List<Long> resultList = new ArrayList<>(ids.length);
            for (String s : ids) {
                resultList.add(Long.valueOf(s));
            }
            cartItemList = cartService.listPartCart(getMemberId(),resultList);
        }

        if (!isPublished(cartItemList)) {
            return new CommonResult().failed("contains not published product");
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductAttr(cartItem.getProductAttr());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductPic(cartItem.getProductPic());
            orderItem.setProductSn(cartItem.getProductSn());
            orderItem.setProductPrice(cartItem.getPrice());
            orderItem.setProductQuantity(cartItem.getQuantity());
            orderItem.setProductSkuId(cartItem.getProductSkuId());
            orderItem.setProductSkuCode(cartItem.getProductSkuCode());
            orderItem.setProductCategoryId(cartItem.getProductCategoryId());
            orderItemList.add(orderItem);
        }

        //计算order_item的实付金额
        handleRealAmount(orderItemList);
        Order order = new Order();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(calcTotalAmount(orderItemList));
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPayAmount(calcPayAmount(order));
        //转化为订单信息并插入数据库
        order.setMemberId(getMemberId());
        order.setCreateTime(new Date());
        order.setMemberUsername(String.valueOf(getMemberId()));
        order.setPayType(payType);
        order.setStatus(PayConstant.PayStatus.INIT.getInt());
        order.setNotifyUrl(notifyUrl);
        //TODO
        //MemberReceiveAddress address = addressService.getById(orderParam.getAddressId());
        //order.setReceiverName(address.getName());
        //order.setReceiverPhone(address.getPhoneNumber());
        //order.setDeleteStatus(0);

        order.setOrderSn(orderSn);
        save(order);
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        orderItemService.saveBatch(orderItemList);

        //删除购物车中的下单商品
        deleteCartItemList(cartItemList, getMemberId());
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);
        return new CommonResult().success(result);
    }
    private boolean isPublished(List<CartItem> orderItems){
        for(CartItem cartItem:orderItems){
            if(productService.getById(cartItem.getProductId()).getPublishStatus()==0) return false;
        }
        return true;
    }
    private void handleRealAmount(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            BigDecimal realAmount = orderItem.getProductPrice()
                    .subtract(orderItem.getPromotionAmount());
            orderItem.setRealAmount(realAmount);
        }
    }
    /**
     * 计算总金额
     */
    private BigDecimal calcTotalAmount(List<OrderItem> orderItemList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OrderItem item : orderItemList) {
            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
        }
        return totalAmount;
    }
    /**
     * 计算订单优惠
     */
    private BigDecimal calcPromotionAmount(List<OrderItem> orderItemList) {
        BigDecimal promotionAmount = new BigDecimal(0);
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getPromotionAmount() != null) {
                promotionAmount = promotionAmount.add(orderItem.getPromotionAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return promotionAmount;
    }
    /**
     * 计算订单应付金额
     */
    private BigDecimal calcPayAmount(Order order) {
        //总金额-促销优惠
        BigDecimal payAmount = order.getTotalAmount()
                .subtract(order.getPromotionAmount());
        return payAmount;
    }

    private void deleteCartItemList(List<CartItem> cartPromotionItemList, Long memberId) {
        List<Long> ids = new ArrayList<>();
        for (CartItem cartPromotionItem : cartPromotionItemList) {
            ids.add(cartPromotionItem.getId());
        }
        cartService.delete(memberId, ids);
    }

}
