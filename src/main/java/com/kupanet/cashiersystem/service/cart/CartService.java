package com.kupanet.cashiersystem.service.cart;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kupanet.cashiersystem.model.CartItem;
import com.kupanet.cashiersystem.model.CartPromotionItem;
import com.kupanet.cashiersystem.model.Product;

import java.util.List;

public interface CartService  {

    CartItem selectById(Long memberId,Long id);

    /**
     * 批量删除购物车中的商品
     */
    int delete(Long memberId, List<Long> ids);
    boolean delete(Long memberId, Long id);
    /**
     * 清空购物车
     */
    int clear(Long memberId);

    boolean setCart(Long memberId,CartItem cartItem);
}
