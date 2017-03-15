package com.demo.bean;

import java.util.Date;

/**
 * 商品
 *
 * @author chenzhongwei
 * @create 2017-03-15 10:26
 **/
public class Product {

    private Integer id;
    private String firstCategory;
    private String secondCategory;
    private String skuId;
    private String productName;
    private String shopId;
    private String shopName;
    private String imageUrl;
    private Integer isSelfSupport; //1.自营  2.第三方店铺
    private Integer goodCount;
    private Integer generalCount;
    private Integer poorCount;
    private Double price;
    private Date spiderDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstCategory() {
        return firstCategory;
    }

    public void setFirstCategory(String firstCategory) {
        this.firstCategory = firstCategory;
    }

    public String getSecondCategory() {
        return secondCategory;
    }

    public void setSecondCategory(String secondCategory) {
        this.secondCategory = secondCategory;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getIsSelfSupport() {
        return isSelfSupport;
    }

    public void setIsSelfSupport(Integer isSelfSupport) {
        this.isSelfSupport = isSelfSupport;
    }

    public Integer getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(Integer goodCount) {
        this.goodCount = goodCount;
    }

    public Integer getGeneralCount() {
        return generalCount;
    }

    public void setGeneralCount(Integer generalCount) {
        this.generalCount = generalCount;
    }

    public Integer getPoorCount() {
        return poorCount;
    }

    public void setPoorCount(Integer poorCount) {
        this.poorCount = poorCount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getSpiderDate() {
        return spiderDate;
    }

    public void setSpiderDate(Date spiderDate) {
        this.spiderDate = spiderDate;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", firstCategory='" + firstCategory + '\'' +
                ", secondCategory='" + secondCategory + '\'' +
                ", skuId='" + skuId + '\'' +
                ", productName='" + productName + '\'' +
                ", shopId='" + shopId + '\'' +
                ", shopName='" + shopName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isSelfSupport=" + isSelfSupport +
                ", goodCount=" + goodCount +
                ", generalCount=" + generalCount +
                ", poorCount=" + poorCount +
                ", price=" + price +
                ", spiderDate=" + spiderDate +
                '}';
    }
}