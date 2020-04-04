package com.kupanet.cashiersystem.service.product;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kupanet.cashiersystem.model.Product;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 */
public interface ProductService  {
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    int create(Product productParam);

    /**
     * 更新商品
     */

    int update(Long id, Product productParam);

    /**
     * 批量修改商品上架状态
     */
    int updatePublishStatus(List<Long> ids, Integer publishStatus);

    /**
     * 批量修改商品推荐状态
     */
    int updateRecommendStatus(List<Long> ids, Integer recommendStatus);

    /**
     * 批量修改新品状态
     */
    int updateNewStatus(List<Long> ids, Integer newStatus);

    /**
     * 根据商品名称或者货号模糊查询
     */
    List<Product> list(String keyword);

    boolean removeById(Serializable var1);

    boolean removeByIds(Collection<? extends Serializable> var1);
    Product getById(Serializable var1);
    List<Product> getByCategoryId(Long categoryId);


    IPage<Product> page(IPage<Product> var1, Wrapper<Product> var2);

}

