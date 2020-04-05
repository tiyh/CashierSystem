package com.kupanet.cashiersystem.service.cart.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kupanet.cashiersystem.model.CartItem;
import com.kupanet.cashiersystem.model.Product;
import com.kupanet.cashiersystem.service.IDGeneratorService;
import com.kupanet.cashiersystem.service.cart.CartService;
import com.kupanet.cashiersystem.service.product.ProductService;
import com.kupanet.cashiersystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final static ObjectMapper mapper = new ObjectMapper();


    private String getHKey(Long memberId) {
        return PREFIX + String.valueOf(memberId);
    }

    @Override
    public CartItem selectById(Long memberId, Long id) {
        return (CartItem) redisUtil.hget(getHKey(memberId), String.valueOf(id));
    }

    @Override
    public List<CartItem> list(Long memberId) {
        List<Object> cartList_object = (List<Object>) redisUtil.hmgetValues(getHKey(memberId));
        List<CartItem> cartList_redis = new ArrayList<>();
        for(Object object:cartList_object){
            cartList_redis.add(mapper.convertValue(object,CartItem.class));
        }
        return cartList_redis;
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
    public List<CartItem> mergeCartList(Long memberId, List<CartItem> cartListTo, List<CartItem> cartListFrom) {
        for (CartItem cartItem : cartListFrom) {
            cartListTo = addGoodsToCartList(memberId,cartListTo, cartItem.getId(), cartItem.getQuantity());
        }
        return cartListTo;
    }
    //TODO addGoodsToCartList String productAttr

    public List<CartItem> addGoodsToCartList(Long memberId,List<CartItem> cartList, Long cartItemId, Integer num) {
        Product product = productService.getById(cartItemId);
        if(product==null){
            throw new RuntimeException("product not exist");
        }
        if(product.getPublishStatus()==0){
            throw new RuntimeException("product PublishStatus error");
        }
        CartItem cartItem = selectById(memberId,cartItemId);
        if(cartItem==null){
            cartItem = createCartItemFromProduct(memberId,product,num);
            setCart(memberId,cartItem);
        }else{
            cartItem.setQuantity(cartItem.getQuantity()+num);
            if(cartItem.getQuantity()<=0){
                delete(memberId,cartItemId);
            }else setCart(memberId, cartItem);
        }
        return cartList;
    }
    private CartItem createCartItemFromProduct(Long memberId,Product product,int num){
        if(num<=0){
            throw new RuntimeException("illegal number");
        }
        if(product==null){
            throw new RuntimeException("product is null");
        }
        CartItem cartItem = new CartItem(product);
        //Random rnd = new Random();
        //cartItem.setId(rnd.nextLong());
        cartItem.setId(Long.parseLong(idGeneratorService.getId()));
        cartItem.setQuantity(num);
        cartItem.setMemberId(memberId);
        return cartItem;
    }
    @Override
    public CartItem createCartItemFromProduct(Long memberId,Long productId,int num){
        Product product = productService.getById(productId);
        return createCartItemFromProduct(memberId,product,num);
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    @Override
    public List<CartItem> listPartCart(Long memberId, List<Long> ids) {
        List<CartItem> cartItemList = new ArrayList<>();
        for(Long id:ids){
            cartItemList.add(selectById(memberId,id));
        }
        return cartItemList;
    }


}