package com.kupanet.cashiersystem.service.cart.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.kupanet.cashiersystem.model.CartItem;
import com.kupanet.cashiersystem.model.CartPromotionItem;
import com.kupanet.cashiersystem.model.Product;
import com.kupanet.cashiersystem.service.cart.CartService;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CartServiceImpl extends ServiceImpl<BaseMapper<CartItem>, CartItem> implements CartService {
    private final static String PREFIX="cart-";
    @Autowired
    private RedisUtil redisUtil;
    private String getHKey(Long memberId){
        return PREFIX+String.valueOf(memberId);
    }

    @Override
    public CartItem add(Long memberId,CartItem cartItem) {
        return add(memberId,cartItem,1);
    }
    @Override
    public CartItem add(Long memberId,CartItem cartItem,int num) {
        //login
        //if(redisUtil.hHasKey(PREFIX+String.valueOf(memberId),String.valueOf(cartItem.getId()))){
        redisUtil.hincr(getHKey(memberId),String.valueOf(cartItem.getId()),num);
        return (CartItem) redisUtil.hget(getHKey(memberId),String.valueOf(cartItem.getId()));
        //Gson gs = new Gson();

        //List<CartItem> feeList = gs.fromJson(JSON字符串/JSON数组,CartItem.class);
        //return null;
    }

    @Override
    public List<CartItem> list(Long memberId, List<Long> ids) {
        List<CartItem> lci = new ArrayList<>();
        for(Long id:ids){
            CartItem ci = (CartItem)redisUtil.hget(getHKey(memberId),String.valueOf(id));
            if(ci!=null) lci.add(ci);
        }
        return lci;
    }

    @Override
    public CartItem selectById(Long id) {
        return null;
    }

    @Override
    public List<CartPromotionItem> listPromotion(Long memberId, List<Long> ids) {
        return null;
    }

    @Override
    public int updateQuantity(Long id, Long memberId, Integer quantity) {
        return 0;
    }

    @Override
    public int delete(Long memberId, List<Long> ids) {
        int successNum=0;
        for(Long id:ids){
            successNum+=redisUtil.hdel(getHKey(memberId),String.valueOf(id));
        }
        return successNum;
    }

    @Override
    public Product getCartProduct(Long productId) {
        return null;
    }

    @Override
    public int updateAttr(CartItem cartItem) {
        return 0;
    }

    @Override
    public int clear(Long memberId) {
        return 0;
    }

    @Override
    public List<CartPromotionItem> calcCartPromotion(List<CartItem> cartItemList) {
        return null;
    }

    @Override
    public CartItem addCart(CartItem cartItem) {
        return null;
    }
}
