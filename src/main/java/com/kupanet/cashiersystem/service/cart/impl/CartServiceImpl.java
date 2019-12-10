package com.kupanet.cashiersystem.service.cart.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kupanet.cashiersystem.model.CartItem;
import com.kupanet.cashiersystem.service.cart.CartService;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl  implements CartService {
    private final static String PREFIX="cart-";
    @Autowired
    private RedisUtil redisUtil;
    private String getHKey(Long memberId){
        return PREFIX+String.valueOf(memberId);
    }



    //public CartItem add(Long memberId,CartItem cartItem,int num) {
    //    redisUtil.hincr(getHKey(memberId),String.valueOf(cartItem.getId()),num);
    //    return (CartItem) redisUtil.hget(getHKey(memberId),String.valueOf(cartItem.getId()));
    //}


    @Override
    public CartItem selectById(Long memberId,Long id) {
        return (CartItem) redisUtil.hget(getHKey(memberId),String.valueOf(id));
    }
    @Override
    public Map<Object,Object> list(Long memberId) {
        return (Map<Object,Object> ) redisUtil.hmget(getHKey(memberId));
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
    public boolean delete(Long memberId, Long id) {
        return redisUtil.hdel(getHKey(memberId),String.valueOf(id))==1;
    }

    @Override
    public int clear(Long memberId) {
        return (int)redisUtil.hclear(getHKey(memberId));
    }

    @Override
    public boolean setCart(Long memberId,CartItem cartItem) {
        return redisUtil.hset(getHKey(memberId),String.valueOf(cartItem.getId()),cartItem);
    }
}
