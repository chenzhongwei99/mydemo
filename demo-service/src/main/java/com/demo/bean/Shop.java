package com.demo.bean;

/**
 * 店铺
 *
 * @author chenzhongwei
 * @create 2017-03-17 15:29
 **/
public class Shop {
    private Integer id;
    private String skuId;
    private String shopId;
    private String shopName;
    private Integer isSelfSupport = 2; //1.自营  2.第三方店铺
    private String venderId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
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

    public Integer getIsSelfSupport() {
        return isSelfSupport;
    }

    public void setIsSelfSupport(Integer isSelfSupport) {
        this.isSelfSupport = isSelfSupport;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", skuId='" + skuId + '\'' +
                ", shopId='" + shopId + '\'' +
                ", shopName='" + shopName + '\'' +
                ", isSelfSupport=" + isSelfSupport +
                ", venderId='" + venderId + '\'' +
                '}';
    }
}