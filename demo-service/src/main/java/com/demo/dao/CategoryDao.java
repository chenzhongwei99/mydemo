package com.demo.dao;


import com.demo.bean.Category;

import java.util.List;

/**
 * 类别DAO接口
 *
 * @author chenzhongwei
 * @create 2017-03-10 15:52
 **/
public interface CategoryDao {

    public void insertCategory(List<Category> categoryList);
}


