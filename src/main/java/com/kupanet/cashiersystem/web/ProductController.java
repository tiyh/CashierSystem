package com.kupanet.cashiersystem.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kupanet.cashiersystem.model.Product;
import com.kupanet.cashiersystem.service.product.ProductService;
import com.kupanet.cashiersystem.util.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 商品信息
 * </p>
 *
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService productService;

    @GetMapping(value = "/list")
    public Object getPmsProductByPage(Product entity,
                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize
    ) {
        try {
            return new CommonResult().success(productService.page(new Page<Product>(pageNum, pageSize), new QueryWrapper<>(entity)));
        } catch (Exception e) {
            logger.error("根据条件查询所有商品信息列表：%s", e.getMessage(), e);
        }
        return new CommonResult().failed();
    }


    @PostMapping(value = "/create")
    public Object savePmsProduct(@RequestBody Product productParam) {
        try {
            int count = productService.create(productParam);
            if (count > 0) {
                return new CommonResult().success(count);
            } else {
                return new CommonResult().failed();
            }
        } catch (Exception e) {
            logger.error("保存商品信息：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }

    }

    @PostMapping(value = "/update/{id}")
    public Object updatePmsProduct(@PathVariable Long id, @RequestBody Product productParam) {
        try {
            int count = productService.update(id, productParam);
            if (count > 0) {
                return new CommonResult().success(count);
            } else {
                return new CommonResult().failed();
            }
        } catch (Exception e) {
            logger.error("更新商品信息：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }

    }

    @DeleteMapping(value = "/delete/{id}")
    public Object deletePmsProduct( @PathVariable Long id) {
        try {
            if (id==null||id==0) {
                return new CommonResult().paramFailed("商品信息id");
            }
            if (productService.removeById(id)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            logger.error("删除商品信息：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @GetMapping(value = "/{id}")
    public Object getPmsProductById( @PathVariable Long id) {
        try {
            if (id==null||id==0) {
                return new CommonResult().paramFailed("商品信息id");
            }
            Product coupon = productService.getById(id);
            return new CommonResult().success(coupon);
        } catch (Exception e) {
            logger.error("查询商品信息明细：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }

    }

    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
        boolean count = productService.removeByIds(ids);
        if (count) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @RequestMapping(value = "/updateInfo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object getUpdateInfo(@PathVariable Long id) {
        Product productResult = productService.getUpdateInfo(id);
        return new CommonResult().success(productResult);
    }



    @RequestMapping(value = "/update/publishStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updatePublishStatus(@RequestParam("ids") List<Long> ids,
                                      @RequestParam("publishStatus") Integer publishStatus) {
        int count = productService.updatePublishStatus(ids, publishStatus);
        if (count > 0) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @RequestMapping(value = "/update/recommendStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updateRecommendStatus(@RequestParam("ids") List<Long> ids,
                                        @RequestParam("recommendStatus") Integer recommendStatus) {
        int count = productService.updateRecommendStatus(ids, recommendStatus);
        if (count > 0) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @RequestMapping(value = "/update/newStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updateNewStatus(@RequestParam("ids") List<Long> ids,
                                  @RequestParam("newStatus") Integer newStatus) {
        int count = productService.updateNewStatus(ids, newStatus);
        if (count > 0) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @RequestMapping(value = "/update/deleteStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updateDeleteStatus(@RequestParam("ids") List<Long> ids,
                                     @RequestParam("deleteStatus") Integer deleteStatus) {
        int count = productService.updateDeleteStatus(ids, deleteStatus);
        if (count > 0) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }
}