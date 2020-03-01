package com.kupanet.cashiersystem.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 订单中所包含的商品
 */
@Setter
@Getter
@TableName("cs_order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 订单编号
     */
    @TableField("order_sn")
    private String orderSn;

    @TableField("product_id")
    private Long productId;

    @TableField("product_pic")
    private String productPic;

    @TableField("product_name")
    private String productName;

    @TableField("product_sn")
    private String productSn;

    /**
     * 销售价格
     */
    @TableField("product_price")
    private BigDecimal productPrice;

    /**
     * 购买数量
     */
    @TableField("product_quantity")
    private Integer productQuantity;

    /**
     * 商品sku编号
     */
    @TableField("product_sku_id")
    private Long productSkuId;

    /**
     * 商品sku条码
     */
    @TableField("product_sku_code")
    private String productSkuCode;

    /**
     * 商品分类id
     */
    @TableField("product_category_id")
    private Long productCategoryId;

    /**
     * 商品的销售属性
     */
    private String sp1;

    private String sp2;

    private String sp3;


    /**
     * 商品销售属性:[{"key":"颜色","value":"颜色"},{"key":"容量","value":"4G"}]
     */
    @TableField("product_attr")
    private String productAttr;


    @TableField("promotion_amount")
    private BigDecimal promotionAmount;

    @TableField("real_amount")
    private BigDecimal realAmount;

    public OrderItem(){

    }



    @Override
    public String toString() {
        return "OrderItem{" +
                ", id=" + id +
                ", orderId=" + orderId +
                ", orderSn=" + orderSn +
                ", productId=" + productId +
                ", productPic=" + productPic +
                ", productName=" + productName +
                ", productSn=" + productSn +
                ", productPrice=" + productPrice +
                ", productQuantity=" + productQuantity +
                ", productSkuId=" + productSkuId +
                ", productSkuCode=" + productSkuCode +
                ", productCategoryId=" + productCategoryId +
                ", sp1=" + sp1 +
                ", sp2=" + sp2 +
                ", sp3=" + sp3 +
                ", productAttr=" + productAttr +
                "}";
    }
}