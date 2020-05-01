package com.kupanet.cashiersystem.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kupanet.cashiersystem.constant.WebConstant;
import com.kupanet.cashiersystem.model.CartItem;
import com.kupanet.cashiersystem.service.cart.CartService;
import com.kupanet.cashiersystem.service.member.MemberService;
import com.kupanet.cashiersystem.util.CommonResult;
import com.kupanet.cashiersystem.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebEndpoint;
import java.util.List;
import java.util.ArrayList;

@RestController
public class CartController {
    private final Logger logger = LoggerFactory.getLogger(CartController.class);
    public final static ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private CartService cartService;
    @Autowired
    private MemberService memberService;

    @GetMapping(value = "/cart/all")
    public Object listCartItem(HttpServletRequest request, HttpServletResponse response) {

        List<CartItem> cartList_cookie = getCartListFromCookie(request,response);
        Long memberId = memberService.getMemberIdFromRequest(request);
        if(memberId < 0L){
            return new CommonResult().success(cartList_cookie);
        }else{
            try{
                List<CartItem> cartList_redis = cartService.list(memberId);
                if(cartList_cookie.size()>0){
                    //merge,cartList_redis at first
                    cartList_redis=cartService.mergeCartList(memberId,cartList_redis, cartList_cookie);
                    //rm cookie
                    CookieUtil.deleteCookie(response, WebConstant.cartCookieName);
                }
                return new CommonResult().success(cartList_redis);
            }catch (Exception e){
                logger.error("根据条件查询购物车表列表：%s", e.getMessage(), e);
            }
        }
        return new CommonResult().failed();
    }

    @GetMapping(value = "/cart/{id}")
    public Object getCartItemById(@PathVariable Long id,HttpServletRequest request, HttpServletResponse response) {
        Long memberId = memberService.getMemberIdFromRequest(request);
        if(memberId<0){
            try {
                if (id==null||id==0) {
                    return new CommonResult().paramFailed("购物车表id");
                }
                CartItem coupon = cartService.selectById(memberId,id);
                return new CommonResult().success(coupon);
            } catch (Exception e) {
                logger.error("查询购物车表明细：%s", e.getMessage(), e);
                return new CommonResult().failed();
            }
        }else {
            List<CartItem> resultList = getCartListFromCookie(request,response);
            for(CartItem ci:resultList){
                if(ci.getId()==id) return new CommonResult().success(ci);
            }
            return new CommonResult().success("not exist");
        }

    }

    @PostMapping(value = "/cart")
    public Object createCartItem(@RequestParam Long productId,int num,
                                 HttpServletRequest request, HttpServletResponse response) {
        Long memberId = memberService.getMemberIdFromRequest(request);
        logger.info("createCartItem memberId:{}", memberId);
        try {
            if(memberId<0L){ //not login ,save to cookie
                List<CartItem> cartList_cookie = getCartListFromCookie(request,response);
                for(CartItem cartItem:cartList_cookie){
                    if(cartItem.getId()==productId){
                        if(cartItem.getQuantity()+num<=0){
                            cartList_cookie.remove(cartItem);
                        }else cartItem.setQuantity(cartItem.getQuantity()+num);
                        CookieUtil.setCookie(response, WebConstant.cartCookieName,
                                mapper.writeValueAsString(cartList_cookie),3600*24 );
                        return new CommonResult().success(cartList_cookie);
                    }
                }
                cartList_cookie.add(cartService.createCartItemFromProduct(memberId,productId,num));
                CookieUtil.setCookie(response, WebConstant.cartCookieName,
                        mapper.writeValueAsString(cartList_cookie),3600*24);
                logger.info("set cookie:{}",mapper.writeValueAsString(cartList_cookie));
                return new CommonResult().success(cartList_cookie);
            }else{//login，save to redis
                return new CommonResult().success(cartService.addGoodsToCartList(
                        memberId, cartService.list(memberId),productId,num));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new CommonResult().failed();
    }


    @PutMapping(value = "/cart")
    public Object updateCartItem(@RequestBody CartItem newCartItem,HttpServletRequest request, HttpServletResponse response) {
        if(newCartItem.getQuantity()<=0){
            return new CommonResult().failed("illegal number");
        }
        Long memberId = memberService.getMemberIdFromRequest(request);
        try {
            if(memberId<0){ //not login ,save to cookie
                List<CartItem> cartList_cookie = getCartListFromCookie(request,response);
                for(CartItem cartItem:cartList_cookie){
                    if(cartItem.getId()==newCartItem.getId()){
                        cartList_cookie.set(cartList_cookie.indexOf(cartItem),newCartItem);
                        CookieUtil.setCookie(response, WebConstant.cartCookieName,
                                mapper.writeValueAsString(cartList_cookie),3600*24 );
                        return new CommonResult().success(cartList_cookie);
                    }
                }
                cartList_cookie.add(newCartItem);
                CookieUtil.setCookie(response, WebConstant.cartCookieName,
                        mapper.writeValueAsString(cartList_cookie),3600*24);
            }else{//login，save to redis
                cartService.setCart(memberId,newCartItem);
                List<CartItem>  resultList= cartService.list(memberId);
                return new CommonResult().success(cartService.list(memberId));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new CommonResult().failed();
    }


    @DeleteMapping(value = "/cart/{id}")
    public Object deleteCartItem(@PathVariable Long id,
                                 HttpServletRequest request, HttpServletResponse response) {
        Long memberId = memberService.getMemberIdFromRequest(request);
        if(memberId<0L){
            try {
                if (id==null||id==0) {
                    return new CommonResult().paramFailed("购物车表id");
                }
                if (cartService.delete(memberId,id)){
                    return new CommonResult().success();
                }
            } catch (Exception e) {
                logger.error("删除购物车表：%s", e.getMessage(), e);
                return new CommonResult().failed();
            }
        }else {
            List<CartItem> cartList_cookie = getCartListFromCookie(request,response);
            for(CartItem cartItem:cartList_cookie){
                if(cartItem.getId()==id){
                    cartList_cookie.remove(cartList_cookie.indexOf(cartItem));
                    try {
                        CookieUtil.setCookie(response, WebConstant.cartCookieName,
                                mapper.writeValueAsString(cartList_cookie),3600*24 );
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return new CommonResult().failed();

                    }
                    return new CommonResult().success(cartList_cookie);
                }
            }
        }
        return new CommonResult().failed();
    }
    @DeleteMapping(value = "/cart/all")
    public Object clearCartItem(HttpServletRequest request,HttpServletResponse response) {
        Long memberId = memberService.getMemberIdFromRequest(request);

        if(memberId<0){
            try {
                int num = cartService.clear(memberId);
                if (num>0){
                    return new CommonResult().success(num);
                }
            } catch (Exception e) {
                logger.error("删除购物车表：%s", e.getMessage(), e);
                return new CommonResult().failed();
            }
        }else {
            CookieUtil.setCookie(response, WebConstant.cartCookieName,
                    "",0);
            return new CommonResult().success();

        }
        return new CommonResult().failed();
    }



    @DeleteMapping(value = "/cart/batch")
    public Object deleteBatch(@RequestParam("ids") List<Long> ids,
                              HttpServletRequest request, HttpServletResponse response) {
        Long memberId = memberService.getMemberIdFromRequest(request);
        if(memberId<0){
            int count = cartService.delete(memberId,ids);
            if (count==ids.size()) {
                return new CommonResult().success(count);
            } else {
                return new CommonResult().failed();
            }
        }else {
            int completeNum = 0;
            List<CartItem> cartList_cookie = getCartListFromCookie(request,response);
            for(CartItem cartItem:cartList_cookie){
                if(ids.contains(cartItem.getId())){
                    cartList_cookie.remove(cartList_cookie.indexOf(cartItem));
                    try {
                        CookieUtil.setCookie(response, WebConstant.cartCookieName,
                                    mapper.writeValueAsString(cartList_cookie),3600*24 );
                        completeNum++;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(completeNum==ids.size()) return new CommonResult().success(cartList_cookie);
            else return new CommonResult().failed(String.valueOf(completeNum));
        }
    }
    private List<CartItem> getCartListFromCookie(HttpServletRequest request, HttpServletResponse response){
        //SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString  = CookieUtil.getCookie(request, WebConstant.cartCookieName);
        if(cartListString==null || cartListString.equals("")){
            cartListString="[]";
        }

        JavaType javaType = getCollectionType(ArrayList.class, CartItem.class);
        List<CartItem> cartList_cookie = null;
        try {
            cartList_cookie = (List<CartItem>)mapper.readValue(cartListString, javaType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return cartList_cookie;
    }


    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}

