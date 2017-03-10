package com.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.bean.Category;
import com.demo.common.DataBaseUtils;
import com.demo.dao.CategoryDao;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类别测试类
 *
 * @author chenzhongwei
 * @create 2017-03-08 11:34
 **/
public class CategoryTest implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3);

    public static void main(String[] args) {
        CategoryTest categoryTest = new CategoryTest();
        Spider spider = Spider.create(categoryTest);
        spider.addUrl("https://baby.jd.com");
//        spider.addUrl("https://list.jd.com/list.html?cat=1319,1526,7061&go=0");
        spider.thread(1).run();
    }

    public void process(Page page) {
        String rawText = page.getRawText();
        Document document = page.getHtml().getDocument();
        Elements scriptElements = document.getElementsByTag("script");
        Element categoryElement = scriptElements.get(4);
        String categoryHTML = categoryElement.html();
        String categoryJsonStr = categoryHTML.replace("(function(window){\n" +
                "    window.data = window.data || {};\n" +
                "    window.data['baby_banner_2'] =", "").replace(",\n" +
                "      portal_floor_id:'2',\n" +
                "      portal_id:'1024',\n" +
                "      isWide: pageConfig.compatible && pageConfig.wideVersion", "").replace("};\n" +
                "  })(window);", "}");
        System.out.println(categoryJsonStr);
        List<Category> categoryList = new ArrayList<Category>();
        try {
            JSONObject categoryJson = JSONObject.parseObject(categoryJsonStr);
            if (categoryJson != null) {
                Date date = new Date();
                JSONArray categoryJSONArray = categoryJson.getJSONArray("navFirst");
                if (categoryJSONArray != null && categoryJSONArray.size() > 0) {
                    for (int i = 0; i < categoryJSONArray.size(); i++) {
                        JSONObject firstJSONObject = categoryJSONArray.getJSONObject(i);
                        if (firstJSONObject != null) {
                            String secondKey = "navThird" + (i + 1);
                            JSONArray secondJSONArray = categoryJson.getJSONArray(secondKey);
                            if (secondJSONArray != null && secondJSONArray.size() > 0) {
                                for (int j = 0; j < secondJSONArray.size(); j++) {
                                    JSONObject secondJSONObject = secondJSONArray.getJSONObject(j);
                                    if (secondJSONObject != null) {
                                        JSONArray thirdJSONArray = secondJSONObject.getJSONArray("children");
                                        if (thirdJSONArray != null && thirdJSONArray.size() > 0) {
                                            for (int k = 0; k < thirdJSONArray.size(); k++) {
                                                JSONObject thirdJSONObject = thirdJSONArray.getJSONObject(k);
                                                if (thirdJSONObject != null) {
                                                    Category category = new Category();
                                                    category.setSpiderDate(date);
                                                    category.setCategoryName(firstJSONObject.getString("NAME"));
                                                    category.setCategoryUrl(firstJSONObject.getString("URL"));
                                                    category.setChildUrl(thirdJSONObject.getString("URL"));
                                                    categoryList.add(category);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                SqlSession sqlSession = DataBaseUtils.sessionFactory.openSession();
                CategoryDao categoryDao = sqlSession.getMapper(CategoryDao.class);
                categoryDao.insertCategory(categoryList);
                sqlSession.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Site getSite() {
        return site;
    }
}