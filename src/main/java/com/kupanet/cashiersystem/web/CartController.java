package com.kupanet.cashiersystem.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kupanet.cashiersystem.model.CartItem;
import com.kupanet.cashiersystem.service.cart.CartService;
import com.kupanet.cashiersystem.util.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final Logger logger = LoggerFactory.getLogger(CartController.class);
    @Autowired
    private CartService cartService;


    @GetMapping(value = "/list")
    public Object getCartItemByPage(CartItem entity,
                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize
    ) {
        try {
            return new CommonResult().success(cartService.page(new Page<CartItem>(pageNum, pageSize), new QueryWrapper<>(entity)));
        } catch (Exception e) {
            logger.error("根据条件查询所有购物车表列表：%s", e.getMessage(), e);
        }
        return new CommonResult().failed();
    }


    @PostMapping(value = "/create")
    public Object saveOmsCartItem(@RequestBody CartItem entity) {
        try {
            if (cartService.save(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            logger.error("保存购物车表：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }


    @PostMapping(value = "/update/{id}")
    public Object updateOmsCartItem(@RequestBody CartItem entity) {
        try {
            if (cartService.updateById(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            logger.error("更新购物车表：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }


    @DeleteMapping(value = "/delete/{id}")
    public Object deleteOmsCartItem(@PathVariable Long id) {
        try {
            if (id==null||id==0) {
                return new CommonResult().paramFailed("购物车表id");
            }
            if (cartService.removeById(id)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            logger.error("删除购物车表：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @GetMapping(value = "/{id}")
    //@PreAuthorize("hasAuthority('oms:OmsCartItem:read')")
    public Object getOmsCartItemById(@PathVariable Long id) {
        try {
            if (id==null||id==0) {
                return new CommonResult().paramFailed("购物车表id");
            }
            CartItem coupon = cartService.getById(id);
            return new CommonResult().success(coupon);
        } catch (Exception e) {
            logger.error("查询购物车表明细：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }

    }

    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
        boolean count = cartService.removeByIds(ids);
        if (count) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }
}

