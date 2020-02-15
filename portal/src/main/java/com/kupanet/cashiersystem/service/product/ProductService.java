package com.kupanet.cashiersystem.service.product;


import com.baomidou.mybatisplus.extension.service.IService;
import com.kupanet.cashiersystem.model.Product;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 */
public interface ProductService extends IService<Product> {
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    int create(Product productParam);

    /**
     * 根据商品编号获取更新信息
     */
    Product getUpdateInfo(Long id);

    /**
     * 更新商品
     */

    int update(Long id, Product productParam);
    /**
     * 批量修改审核状态
     *
     * @param ids          产品id
     * @param verifyStatus 审核状态
     * @param detail       审核详情
     */
    int updateVerifyStatus(Long ids, Integer verifyStatus, String detail);

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

}

