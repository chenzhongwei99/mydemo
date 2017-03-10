package com.demo.common;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * 数据库工具类
 *
 * @author chenzhongwei
 * @create 2017-03-10 16:38
 **/
public class DataBaseUtils {

    private static final Object object = new Object();

    public static SqlSessionFactory sessionFactory = getSessionFactory();

    /**
     * 创建SqlSessionFactory
     *
     * @return SqlSessionFactory
     * @author chenzhongwei
     */
    private static SqlSessionFactory getSessionFactory() {
        try {
            synchronized (object) {
                if (sessionFactory == null) {
                    sessionFactory = new SqlSessionFactoryBuilder().build(Resources
                            .getResourceAsReader("configuration.xml"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessionFactory;
    }
}