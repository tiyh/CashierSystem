package com.kupanet.cashiersystem.service.cart.impl;

import com.kupanet.cashiersystem.model.CartItem;
import com.kupanet.cashiersystem.model.Product;
import com.kupanet.cashiersystem.service.IDGeneratorService;
import com.kupanet.cashiersystem.service.cart.CartService;
import com.kupanet.cashiersystem.service.product.ProductService;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl  implements CartService {
    private final static String PREFIX = "cart-";
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ProductService productService;

    @Autowired
    private IDGeneratorService idGeneratorService;

    private String getHKey(Long memberId) {
        return PREFIX + String.valueOf(memberId);
    }

    @Override
    public CartItem selectById(Long memberId, Long id) {
        return (CartItem) redisUtil.hget(getHKey(memberId), String.valueOf(id));
    }

    @Override
    public List<Object> list(Long memberId) {
         return redisUtil.hmgetValues(getHKey(memberId));
    }


    @Override
    public int delete(Long memberId, List<Long> ids) {
        int successNum = 0;
        for (Long id : ids) {
            successNum += redisUtil.hdel(getHKey(memberId), String.valueOf(id));
        }
        return successNum;
    }

    @Override
    public boolean delete(Long memberId, Long id) {
        return redisUtil.hdel(getHKey(memberId), String.valueOf(id)) == 1;
    }

    @Override
    public int clear(Long memberId) {
        return (int) redisUtil.hclear(getHKey(memberId));
    }

    @Override
    public boolean setCart(Long memberId, CartItem cartItem) {
        return redisUtil.hset(getHKey(memberId), String.valueOf(cartItem.getId()), cartItem);
    }

    @Override
    public boolean createCart(Long memberId, CartItem cartItem) {
        return redisUtil.hset(getHKey(memberId), String.valueOf(cartItem.getId()), cartItem);
    }

    @Override
    public List<CartItem> mergeCartList(Long memberId, List<CartItem> cartList1, List<CartItem> cartList2) {
        for (CartItem cartItem : cartList2) {
            cartList1 = addGoodsToCartList(memberId,cartList1, cartItem.getId(), cartItem.getQuantity());
        }
        return cartList1;
    }

    public List<CartItem> addGoodsToCartList(Long memberId,List<CartItem> cartList, Long cartItemId, Integer num) {
        Product product = productService.getById(cartItemId);
        if(product==null){
            throw new RuntimeException("product not exist");
        }
        if(!product.getPublishStatus().equals("1")){
            throw new RuntimeException("product PublishStatus error");
        }
        CartItem cartItem = selectById(memberId,cartItemId);
        if(cartItem==null){
            cartItem = createCartItemFromProduct(memberId,product,num);
            createCart(memberId,cartItem);
        }else{
            cartItem.setQuantity(cartItem.getQuantity()+num);
            if(cartItem.getQuantity()<=0){
                delete(memberId,cartItemId);
            }else setCart(memberId, cartItem);
        }
        return cartList;
    }

    private CartItem createCartItemFromProduct(Long memberId,Product product,Integer num){
        if(num<=0){
            throw new RuntimeException("illegal number");
        }
        CartItem cartItem = new CartItem(product);
        cartItem.setId(Long.parseLong(idGeneratorService.getId()));
        cartItem.setQuantity(num);
        cartItem.setMemberId(memberId);
        return cartItem;
    }
}