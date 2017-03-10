package com.demo.bean;

import java.util.Date;

/**
 * 类别信息
 *
 * @author chenzhongwei
 * @create 2017-03-08 11:29
 **/
public class Category {
    private Integer id;
    private String categoryUrl;
    private String categoryName;
    private String childUrl;
    private Date spiderDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getChildUrl() {
        return childUrl;
    }

    public void setChildUrl(String childUrl) {
        this.childUrl = childUrl;
    }

    public Date getSpiderDate() {
        return spiderDate;
    }

    public void setSpiderDate(Date spiderDate) {
        this.spiderDate = spiderDate;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", categoryUrl='" + categoryUrl + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", childUrl='" + childUrl + '\'' +
                ", spiderDate=" + spiderDate +
                '}';
    }
}