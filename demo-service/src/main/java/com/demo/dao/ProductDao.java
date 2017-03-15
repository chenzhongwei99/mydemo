package com.demo.dao;


import com.demo.bean.Product;

import java.util.List;

/**
 * 商品DAO接口
 *
 * @author chenzhongwei
 * @create 2017-03-10 15:52
 **/
public interface ProductDao {

    /**
     * 添加商品
     *
     * @param productList
     * @author chenzhongwei
     */
    public void insertProduct(List<Product> productList);

    /**
     * 查询当天的商品数据
     *
     * @return 商品集合
     * @author chenzhongwei
     */
    public List<Product> searchProductListByNow();

   /* *//**
     * 更新商品
     *
     * @param productList
     * @author chenzhongwei
     *//*
    public void updateProduct(List<Product> productList);*/
}


