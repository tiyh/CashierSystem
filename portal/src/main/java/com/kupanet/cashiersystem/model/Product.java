package com.kupanet.cashiersystem.model;

import java.math.BigDecimal;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品信息
 */
@Getter
@Setter
@TableName("cs_product")
public class Product implements Serializable {

    private static final long serialVersionUID = -5433238620907696526L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long productCategoryId;

    /**
     * 商品分类名称
     */
    @TableField("product_category_name")
    private String productCategoryName;

    private String name;

    /**
     * 货号
     */
    @TableField("product_sn")
    private String productSn;

    /**
     * 上架状态：0->下架；1->上架
     */
    @TableField("publish_status")
    private Integer publishStatus;

    /**
     * 新品状态:0->不是新品；1->新品
     */
    @TableField("new_status")
    private Integer newStatus;

    /**
     * 推荐状态；0->不推荐；1->推荐
     */
    @TableField("recommand_status")
    private Integer recommandStatus;

    private BigDecimal price;

    /**
     * 促销状态：0->不促销；1->促销
     */
    @TableField("promotion_status")
    private Integer promotionStatus;

    /**
     * 促销价格
     */
    @TableField("promotion_price")
    private BigDecimal promotionPrice;


    /**
     * 排序
     */
    private Integer sort;

    /**
     * 副标题
     */
    @TableField("sub_title")
    private String subTitle;

    /**
     * 商品描述
     */
    private String description;

    private String pic;

    /**
     * 画册图片，连产品图片限制为5张，以逗号分割
     */
    @TableField("album_pics")
    private String albumPics;

    @TableField("detail_title")
    private String detailTitle;

    @TableField("detail_desc")
    private String detailDesc;

    public Product(){
        
    }

    @Override
    public String toString() {
        return "Product{" +
                ", id=" + id +
                ", productCategoryId=" + productCategoryId +
                ", name=" + name +
                ", pic=" + pic +
                ", productSn=" + productSn +
                ", publishStatus=" + publishStatus +
                ", newStatus=" + newStatus +
                ", recommandStatus=" + recommandStatus +
                ", sort=" + sort +
                ", price=" + price +
                ", promotionPrice=" + promotionPrice +
                ", subTitle=" + subTitle +
                ", description=" + description +
                ", albumPics=" + albumPics +
                ", detailTitle=" + detailTitle +
                ", detailDesc=" + detailDesc +
                ", productCategoryName=" + productCategoryName +
                "}";
    }

}