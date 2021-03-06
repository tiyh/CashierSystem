package com.kupanet.cashiersystem.service.cart;

import com.kupanet.cashiersystem.model.CartItem;
import com.kupanet.cashiersystem.model.Product;

import java.util.Collection;
import java.util.List;

public interface CartService  {

    CartItem selectById(Long memberId,Long id);
    List<CartItem> list(Long memberId);

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

    List<CartItem> mergeCartList(Long memberId,List<CartItem> cartList1, List<CartItem> cartList2);

    List<CartItem> addGoodsToCartList(Long memberId,List<CartItem> cartList, Long productId, Integer num);
    CartItem createCartItemFromProduct(Long memberId, Long productId, int num);
    List<CartItem> listPartCart(Long memberId, List<Long> ids);
}
