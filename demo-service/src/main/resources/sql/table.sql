CREATE TABLE `CATEGORY` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CATEGORY_NAME` varchar(10) DEFAULT NULL COMMENT '一级类别的名称',
  `CHILD_URL` varchar(200) DEFAULT NULL COMMENT '三级类别的url',
  `SPIDER_DATE` date DEFAULT NULL COMMENT '爬虫时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7178 DEFAULT CHARSET=utf8



CREATE TABLE `PRODUCT` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `FIRST_CATEGORY` varchar(10) DEFAULT NULL COMMENT '一级类别',
  `SECOND_CATEGORY` varchar(10) DEFAULT NULL COMMENT '二级类别',
  `SKU_ID` varchar(20) DEFAULT NULL COMMENT 'SKUID',
  `PRODUCT_NAME` varchar(200) DEFAULT NULL COMMENT '商品名称',
  `SHOP_ID` varchar(20) DEFAULT NULL COMMENT '铺店ID',
  `SHOP_NAME` varchar(100) DEFAULT NULL COMMENT '铺店名称',
  `IMAGE_URL` varchar(200) DEFAULT NULL COMMENT '品商图片地址',
  `IS_SELF_SUPPORT` int(1) DEFAULT NULL COMMENT '1.自营  2.第三方店铺',
  `GOOD_COUNT` int(10) DEFAULT NULL COMMENT '评数好',
  `GENERAL_COUNT` int(10) DEFAULT NULL COMMENT '一般评价数',
  `POOR_COUNT` int(10) DEFAULT NULL COMMENT '评差数',
  `PRICE` decimal(10,0) DEFAULT NULL COMMENT '商品价格',
  `SPIDER_DATE` date DEFAULT NULL COMMENT '虫爬时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=427780 DEFAULT CHARSET=utf8