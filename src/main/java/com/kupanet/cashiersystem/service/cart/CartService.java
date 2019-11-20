package com.kupanet.cashiersystem.service.cart;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kupanet.cashiersystem.model.CartItem;
import com.kupanet.cashiersystem.model.CartPromotionItem;
import com.kupanet.cashiersystem.model.Product;

import java.util.List;

public interface CartService extends IService<CartItem> {
    /**
     * 查询购物车中是否包含该商品，有增加数量，无添加到购物车
     */

    CartItem add(Long memberId,CartItem cartItem);
    CartItem add(Long memberId,CartItem cartItem,int num);

    /**
     * 根据会员编号获取购物车列表
     */
    List<CartItem> list(Long memberId, List<Long> ids);

    CartItem selectById(Long id);

    /**
     * 获取包含促销活动信息的购物车列表
     */
    List<CartPromotionItem> listPromotion(Long memberId, List<Long> ids);

    /**
     * 修改某个购物车商品的数量
     */
    int updateQuantity(Long id, Long memberId, Integer quantity);

    /**
     * 批量删除购物车中的商品
     */
    int delete(Long memberId, List<Long> ids);

    /**
     * 获取购物车中用于选择商品规格的商品信息
     */
    Product getCartProduct(Long productId);

    /**
     * 修改购物车中商品的规格
     */

    int updateAttr(CartItem cartItem);

    /**
     * 清空购物车
     */
    int clear(Long memberId);

    List<CartPromotionItem> calcCartPromotion(List<CartItem> cartItemList);

    CartItem addCart(CartItem cartItem);
}
