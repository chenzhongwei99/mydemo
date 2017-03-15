package com.demo.service;

import com.demo.bean.Category;
import com.demo.bean.Product;
import com.demo.common.DataBaseUtils;
import com.demo.dao.CategoryDao;
import com.demo.dao.ProductDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.ibatis.session.SqlSession;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商品分页页面测试类
 *
 * @author chenzhongwei
 * @create 2017-03-08 11:34
 **/
public class ProductPage implements PageProcessor {

    private static SqlSession sqlSession = DataBaseUtils.sessionFactory.openSession();

    private static ProductDao productDao = sqlSession.getMapper(ProductDao.class);

    private Site site = Site.me().setRetryTimes(3)
            .addCookie("list.jd.com", "__jda", "122270672.480222635.1486966743.1489471336.1489475875.35")
            .addCookie("list.jd.com", "unpl", "V2_ZzNtbUIFEx0hWEJccxxYAWJWRg4RVkMTcgtGXSxLDwAwURMOclRCFXMUR1ZnGFUUZgsZWUBcQBZFCEdkex5fDGQzF19GXkIVcAt2ZHgZbA1XBhtcQVRAEncMdmR8KWzR8qjK1tUFAlesuOOC0bdsAmIEFFRLUkcldDhHZDB3XUhiChNeQVREF3E4R2R4")
            .addCookie("list.jd.com", "__jdv", "122270672|c.duomai.com|t_16282_43580142|tuiguang|1ca9ea5994554debb0176218fcb4fc0b|1488938002173")
            .addCookie("list.jd.com", "__jdu", "480222635")
            .addCookie("list.jd.com", "ipLoc-djd", "1-72-2799-0")
            .addCookie("list.jd.com", "ipLocation", "北京")
            .addCookie("list.jd.com", "user-key", "92eba3f4-ec68-4c09-bbc3-2ad48ed870b8")
            .addCookie("list.jd.com", "cn", "0")
            .addCookie("list.jd.com", "listck", "927da36d2782d3e044c4f066becd6e94")
            .addCookie("list.jd.com", "areaId", "1")
            .addCookie("list.jd.com", "mt_xid", "V2_52007VwYbU15bUV0dTSlbAjIEFlIJD05fSxlKQABvBRBODQ9UWANIGggMYAIbUQ9RUQ0vShhcA3sCEE5cXkNaFkIZXQ5kASJQbVhiWh5NEFgBbwMRYl1eVl4=")
            .addCookie("list.jd.com", "__jdc", "122270672")
            .addCookie("__jdb", "122270672.3.480222635|35.1489475875");


    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CategoryDao categoryDao = sqlSession.getMapper(CategoryDao.class);
        List<Category> categoryList = categoryDao.searchCategoryListByNow();
        if (CollectionUtils.isNotEmpty(categoryList)) {
            ProductPage productPage = new ProductPage();
            Spider spider = Spider.create(productPage);
            for (Category category : categoryList) {
                if (category != null) {
                    String childUrl = category.getChildUrl();
                    if (StringUtils.isNotEmpty(childUrl)) {
                        spider.addUrl(childUrl);
                    }
                }
            }
            spider.thread(50).run();
        }
        stopWatch.stop();
        System.out.println("商品花费时间----------------------------------- " + stopWatch.getTime());
    }

    public void process(Page page) {
        Date date = new Date();
        Html html = page.getHtml();
        List<Product> productList = new ArrayList<Product>();
        for (int i = 0; i < 60; i++) {
            String skuId = html.xpath("//div[@id='J_searchWrap']/div[@id='J_container']/div[@id='J_main']/div[@class='m-list']/div[@class='ml-wrap']/div[@id='plist']/ul[@class='gl-warp clearfix']/li[" + (i + 1) + "]/div[@class='gl-i-wrap j-sku-item']/@data-sku").toString();
            if (StringUtils.isNotEmpty(skuId)) {
                Product product = new Product();
                product.setFirstCategory(html.xpath("//div[@id='J_crumbsBar']/div[@class='crumbs-bar clearfix']/div[@class='crumbs-nav']/div[@class='crumbs-nav-main clearfix']/div[2]/div[@class='menu-drop']/div[@class='trigger']/span/text()").toString());
                product.setSecondCategory(html.xpath("//div[@id='J_crumbsBar']/div[@class='crumbs-bar clearfix']/div[@class='crumbs-nav']/div[@class='crumbs-nav-main clearfix']/div[3]/div[@class='menu-drop']/div[@class='trigger']/span/text()").toString());
                product.setSkuId(skuId);
                String imageUrl = html.xpath("//div[@id='J_searchWrap']/div[@id='J_container']/div[@id='J_main']/div[@class='m-list']/div[@class='ml-wrap']/div[@id='plist']/ul[@class='gl-warp clearfix']/li[" + (i + 1) + "]/div[@class='gl-i-wrap j-sku-item']/div[@class='p-img']/a/img/@src").toString();
                String dataLazyImg = html.xpath("//div[@id='J_searchWrap']/div[@id='J_container']/div[@id='J_main']/div[@class='m-list']/div[@class='ml-wrap']/div[@id='plist']/ul[@class='gl-warp clearfix']/li[" + (i + 1) + "]/div[@class='gl-i-wrap j-sku-item']/div[@class='p-img']/a/img/@data-lazy-img").toString();
                product.setImageUrl(StringUtils.isNotEmpty(imageUrl) ? imageUrl : dataLazyImg);
                product.setProductName(html.xpath("//div[@id='J_searchWrap']/div[@id='J_container']/div[@id='J_main']/div[@class='m-list']/div[@class='ml-wrap']/div[@id='plist']/ul[@class='gl-warp clearfix']/li[" + (i + 1) + "]/div[@class='gl-i-wrap j-sku-item']/div[@class='p-name']/a/em/text()").toString());
                product.setShopName(html.xpath("//div[@id='J_searchWrap']/div[@id='J_container']/div[@id='J_main']/div[@class='m-list']/div[@class='ml-wrap']/div[@id='plist']/ul[@class='gl-warp clearfix']/li[" + (i + 1) + "]/div[@class='gl-i-wrap j-sku-item']/div[@class='p-shop']/@data-shop_name").toString());
                product.setSpiderDate(date);
                productList.add(product);
            }
        }
        try {
            synchronized (this) {
                productDao.insertProduct(productList);
                sqlSession.commit();
                System.out.println(productList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Site getSite() {
        return site;
    }
}