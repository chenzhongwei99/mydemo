package com.demo.bean;

/**
 * 价格
 *
 * @author chenzhongwei
 * @create 2017-03-17 15:26
 **/
public class Price {
    private Integer id;
    private String skuId;
    private Double price;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", skuId='" + skuId + '\'' +
                ", price=" + price +
                '}';
    }
}