-- ----------------------------
--  Table structure for `cs_order`
-- ----------------------------
DROP TABLE IF EXISTS `cs_order`;
CREATE TABLE `cs_order` (
  `id` bigint(20) NOT NULL COMMENT '订单id',
  `member_id` bigint(20) NOT NULL,
  `order_sn` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `create_time` datetime DEFAULT NULL COMMENT '提交时间',
  `member_username` varchar(512) DEFAULT NULL COMMENT '用户帐号',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '应付金额（实际支付金额）',
  `discount_amount` decimal(10,2) DEFAULT NULL COMMENT '管理员后台调整订单使用的折扣金额',
  `pay_type` int(1) DEFAULT NULL COMMENT '支付方式：0->未支付；1->支付宝；2->微信',
  `status` int(1) DEFAULT NULL COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
  `bill_type` int(1) DEFAULT NULL COMMENT '发票类型：0->不开发票；1->电子发票；2->纸质发票',
  `bill_header` varchar(200) DEFAULT NULL COMMENT '发票抬头',
  `bill_content` varchar(200) DEFAULT NULL COMMENT '发票内容',
  `receiver_name` varchar(100) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) NOT NULL COMMENT '收货人电话',
  `note` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `promotion_amount` decimal(10,2) DEFAULT NULL,
  `notify_url` varchar(512) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8 COMMENT='订单表';

-- ----------------------------
--  Records of `cs_order`
-- ----------------------------
BEGIN;
INSERT INTO `cs_order` VALUES('76','17','20200229010117', '2020-02-19 22:54:34','chris', '9.90','9.90', '0.00', '1', '0','0',null, null,'123', '13888888888', null,  null, null,'0.1',"http://127.0.0.1:8087/notify");
COMMIT;

-- ----------------------------
--  Table structure for `cs_order_item`
-- ----------------------------
DROP TABLE IF EXISTS `cs_order_item`;
CREATE TABLE `cs_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `order_sn` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `product_id` bigint(20) DEFAULT NULL,
  `product_pic` varchar(500) DEFAULT NULL,
  `product_name` varchar(200) DEFAULT NULL,
  `product_sn` varchar(64) DEFAULT NULL,
  `product_price` decimal(10,2) DEFAULT NULL COMMENT '销售价格',
  `product_quantity` int(11) DEFAULT NULL COMMENT '购买数量',
  `product_sku_id` bigint(20) DEFAULT NULL COMMENT '商品sku编号',
  `product_sku_code` varchar(50) DEFAULT NULL COMMENT '商品sku条码',
  `product_category_id` bigint(20) DEFAULT NULL COMMENT '商品分类id',
  `sp1` varchar(100) DEFAULT NULL COMMENT '商品的销售属性',
  `sp2` varchar(100) DEFAULT NULL,
  `sp3` varchar(100) DEFAULT NULL,
  `product_attr` varchar(500) DEFAULT NULL COMMENT '商品销售属性:[{"key":"颜色","value":"颜色"},{"key":"容量","value":"4G"}]',
  `promotion_amount` decimal(10,2) DEFAULT NULL,
  `real_amount` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8 COMMENT='订单中所包含的商品';

-- ----------------------------
--  Records of `cs_order_item`
-- ----------------------------
BEGIN;
INSERT INTO `cs_order_item` VALUES ('1', '76', '20200229010117', '23', '127.0.0.1/images/1522738681.jpg','菜煎饼',  '6946605', '9.90', '1', '149', '101902140026004', '19', null, null, null,'规格:4','0.1','9.8');


-- ----------------------------
--  Table structure for `cs_order_operate_history`
-- ----------------------------
DROP TABLE IF EXISTS `cs_order_operate_history`;
CREATE TABLE `cs_order_operate_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `operate_man` varchar(100) DEFAULT NULL COMMENT '操作人：用户；系统；后台管理员',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  `order_status` int(1) DEFAULT NULL COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
  `note` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COMMENT='订单操作历史记录';

-- ----------------------------
--  Records of `order_operate_history`
-- ----------------------------
BEGIN;
INSERT INTO `cs_order_operate_history` VALUES ('5', '74', '后台管理员', '2020-01-12 14:01:29', '2', '完成');

-- ----------------------------
--  Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `cs_product`;
CREATE TABLE `cs_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_category_id` bigint(20) DEFAULT NULL,
  `product_category_name` varchar(255) DEFAULT NULL COMMENT '商品分类名称',
  `name` varchar(64) NOT NULL,
  `product_sn` varchar(64) NOT NULL COMMENT '货号',
  `publish_status` int(1) DEFAULT NULL COMMENT '上架状态：0->下架；1->上架',
  `new_status` int(1) DEFAULT NULL COMMENT '新品状态:0->不是新品；1->新品',
  `recommand_status` int(1) DEFAULT NULL COMMENT '推荐状态；0->不推荐；1->推荐',
  `price` decimal(10,2) DEFAULT NULL,
  `promotion_status` int(1) DEFAULT 0 COMMENT '促销状态；0->不促销；1->促销',
  `promotion_price` decimal(10,2) DEFAULT NULL COMMENT '促销价格',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `sub_title` varchar(255) DEFAULT NULL COMMENT '副标题',
  `description` text COMMENT '商品描述',
  `pic` varchar(255) DEFAULT NULL,
  `album_pics` varchar(255) DEFAULT NULL COMMENT '画册图片，连产品图片限制为5张，以逗号分割',
  `detail_title` varchar(255) DEFAULT NULL,
  `detail_desc` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8 COMMENT='商品信息';

-- ----------------------------
--  Records of `cs_product`
-- ----------------------------
BEGIN;
INSERT INTO `cs_product` VALUES ('23', '6', '主食', '菜煎饼', 'NO.1098', '1','0','0','9.90','1','8.80','1','煎饼','杂粮煎饼', '127.0.0.1/images/1522738681.jpg', '127.0.0.1/images/1522738681.jpg,127.0.0.1/images/1522738681.jpg', '杂粮煎饼', '测试');
COMMIT;

-- ----------------------------
--  Table structure for `cs_cart_item`
-- ----------------------------
DROP TABLE IF EXISTS `cs_cart_item`;
CREATE TABLE `cs_cart_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL COMMENT '购买数量',
  `price` decimal(10,2) DEFAULT NULL COMMENT '添加到购物车的价格',
  `sp1` varchar(200) DEFAULT NULL COMMENT '销售属性1',
  `sp2` varchar(200) DEFAULT NULL COMMENT '销售属性2',
  `sp3` varchar(200) DEFAULT NULL COMMENT '销售属性3',
  `product_pic` varchar(1000) DEFAULT NULL COMMENT '商品主图',
  `product_name` varchar(500) DEFAULT NULL COMMENT '商品名称',
  `product_sub_title` varchar(500) DEFAULT NULL COMMENT '商品副标题（卖点）',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_date` datetime DEFAULT NULL COMMENT '修改时间',
  `product_category_id` bigint(20) DEFAULT NULL COMMENT '商品分类',
  `product_sn` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8 COMMENT='购物车表';

-- ----------------------------
--  Records of `cs_cart_item`
-- ----------------------------
BEGIN;
INSERT INTO `cs_cart_item` VALUES ('136', '23', '1011', '2', '8.80','鸡蛋', '火腿', null, '127.0.0.1/images/5a9d248cN071f4959.jpg', '菜煎饼', '杂粮煎饼', '2020-02-04 10:06:43', '2020-02-05 10:06:43', '1', 'NO.1098');
COMMIT;

DROP TABLE IF EXISTS `cs_member`;
CREATE TABLE `cs_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(64) DEFAULT NULL COMMENT '手机号码',
  `status` int(1) DEFAULT NULL COMMENT '帐号启用状态:0->禁用；1->启用',
  `create_time` datetime DEFAULT NULL COMMENT '注册时间',
  `password_reset_time` datetime DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='会员表';

BEGIN;
INSERT INTO `cs_member` VALUES ('136', 'hit_15', '123456', 'chris', '13888888888','1','2020-02-04 10:06:43','2020-02-04 10:06:43');
COMMIT;