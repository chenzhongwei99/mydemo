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

    /**
     * 添加类别
     *
     * @param categoryList
     * @author chenzhongwei
     */
    public void insertCategory(List<Category> categoryList);

    /**
     * 查询当天的类别数据
     *
     * @return 类别集合
     * @author chenzhongwei
     */
    public List<Category> searchCategoryListByNow();
}


