package com.kupanet.cashiersystem.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class CartItem implements Serializable {

    private Long id;

    private Long productId;

    private Long memberId;
    private Long productSkuId;
    /**
     * 商品sku条码
     */
    private String productSkuCode;


    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 添加到购物车的价格
     */
    private BigDecimal price;

    /**
     * 销售属性1
     */
    private String sp1;

    /**
     * 销售属性2
     */
    private String sp2;

    /**
     * 销售属性3
     */
    private String sp3;

    /**
     * 商品主图
     */
    private String productPic;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品副标题（卖点）
     */
    private String productSubTitle;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date modifyDate;

    /**
     * 商品分类
     */
    private Long productCategoryId;

    private String productSn;


    @Override
    public String toString() {
        return "CartItem{" +
                ", id=" + id +
                ", productId=" + productId +
                ", memberId=" + memberId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", sp1=" + sp1 +
                ", sp2=" + sp2 +
                ", sp3=" + sp3 +
                ", productPic=" + productPic +
                ", productName=" + productName +
                ", productSubTitle=" + productSubTitle +
                ", createDate=" + createDate +
                ", modifyDate=" + modifyDate +
                ", productCategoryId=" + productCategoryId +
                ", productSn=" + productSn +
                "}";
    }
}
