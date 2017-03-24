package com.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.bean.Category;
import com.demo.common.DataBaseUtils;
import com.demo.dao.CategoryDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

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

    private static SqlSession sqlSession = DataBaseUtils.sessionFactory.openSession();

    private static CategoryDao categoryDao = sqlSession.getMapper(CategoryDao.class);

    private Site site = Site.me().setRetryTimes(3);

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CategoryTest categoryTest = new CategoryTest();
        Spider spider = Spider.create(categoryTest);
        spider.addUrl("https://baby.jd.com");
        spider.thread(50).run();
        stopWatch.stop();
        System.out.println("类别花费时间----------------------------------- " + stopWatch.getTime());
    }

    public void process(Page page) {
        List<Category> categoryList = new ArrayList<Category>();
        String pageUrl = page.getRequest().getUrl();
        Date date = new Date();
        if (pageUrl.contains("https://list.jd.com/list.html")) {
            Html html = page.getHtml();
            String categoryName = html.xpath("//div[@id='J_crumbsBar']/div[@class='crumbs-bar clearfix']/div[@class='crumbs-nav']/div[@class='crumbs-nav-main clearfix']/div[2]/div[@class='menu-drop']/div[@class='trigger']/span/text()").toString();
            String totalPageStr = html.xpath("//div[@id='J_searchWrap']/div[@id='J_container']/div[@id='J_main']/div[@class='m-list']/div[@class='ml-wrap']/div[@class='page clearfix']/div[@id='J_bottomPage']/span[@class='p-skip']/em/b/text()").toString();
            String childUrl = html.xpath("//div[@id='J_searchWrap']/div[@id='J_container']/div[@id='J_main']/div[@class='m-list']/div[@class='ml-wrap']/div[@class='page clearfix']/div[@id='J_bottomPage']/span[@class='p-num']/a[@class='curr']/@href").toString();
            if (StringUtils.isNotEmpty(totalPageStr) && StringUtils.isNotEmpty(childUrl)) {
                Integer totalPage = Integer.valueOf(totalPageStr);
                System.out.println(pageUrl + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + totalPage);
                for (int i = 0; i < totalPage; i++) {
                    Category category = new Category();
                    category.setSpiderDate(date);
                    category.setCategoryName(categoryName);
                    category.setChildUrl(childUrl.replace("page=1", "page=" + (i + 1)));
                    categoryList.add(category);
                }
                try {
                    //高并发存储数据到数据库的时候 要加锁 否则会丢失数据
                    synchronized (this) {
                        System.out.println(categoryList);
                        categoryDao.insertCategory(categoryList);
                        sqlSession.commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
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
            try {

                List<String> list = new ArrayList<String>();

                JSONObject categoryJson = JSONObject.parseObject(categoryJsonStr);
                if (categoryJson != null) {
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
                                                        String childUrl = thirdJSONObject.getString("URL");
                                                        if (childUrl.contains("https://list.jd.com/list.html")) {
                                                            page.addTargetRequest(childUrl);
                                                            list.add(childUrl);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println(pageUrl + " ------------------------------------------ " + list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public Site getSite() {
        return site;
    }
}