package com.demo.bean;

/**
 * 评论
 *
 * @author chenzhongwei
 * @create 2017-03-17 15:31
 **/
public class Comment {
    private Integer id;
    private String skuId;
    private Integer goodCount;
    private Integer generalCount;
    private Integer poorCount;

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

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", skuId='" + skuId + '\'' +
                ", goodCount=" + goodCount +
                ", generalCount=" + generalCount +
                ", poorCount=" + poorCount +
                '}';
    }
}