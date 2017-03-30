package com.demo.service;

import com.demo.bean.*;
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

import java.util.*;
import java.util.concurrent.*;

/**
 * 商品分页页面测试类
 *
 * @author chenzhongwei
 * @create 2017-03-08 11:34
 **/
public class ProductPage implements PageProcessor {

    private static SqlSession sqlSession = DataBaseUtils.sessionFactory.openSession();

    private static ProductDao productDao = sqlSession.getMapper(ProductDao.class);


    private static final ConcurrentHashMap<String, Product> productMap = new ConcurrentHashMap<String, Product>();

    private static final ConcurrentHashMap<String, Price> priceMap = new ConcurrentHashMap<String, Price>();

    private static final ConcurrentHashMap<String, Comment> commentMap = new ConcurrentHashMap<String, Comment>();

    private static final ConcurrentHashMap<String, Shop> shopMap = new ConcurrentHashMap<String, Shop>();

    private static final ConcurrentHashMap<String, Object> errorMap = new ConcurrentHashMap<String, Object>();

    private static final ExecutorService productPool = Executors.newFixedThreadPool(100);

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
            spider.thread(100).run();
        }
        stopWatch.stop();
        System.out.println("商品花费时间----------------------------------- " + stopWatch.getTime());
        System.out.println("商品数量-------------------------------------" + productMap.size());

        stopWatch.reset();
        stopWatch.start();
        int count = productMap.size();
        int productNum_http = 0;
        int currentPage_http = 1;
        int remainder_http = count % 100;
        int totalPage_http = (remainder_http == 0 ? count / 100 : (count / 100) + 1);
        StringBuffer priceBuffer = new StringBuffer();
        StringBuffer commentBuffer = new StringBuffer();
        StringBuffer shopBuffer = new StringBuffer();

        List<Future<Map<String, Object>>> productFutureList = new ArrayList<Future<Map<String, Object>>>();

        for (Map.Entry<String, Product> productEntry : productMap.entrySet()) {
            productNum_http++;
            String skuId = productEntry.getKey();
            priceBuffer.append("J_").append(skuId).append(",");
            commentBuffer.append(skuId).append(",");
            shopBuffer.append(skuId).append(",");
            if ((currentPage_http < totalPage_http && productNum_http % 100 == 0) || (currentPage_http == totalPage_http && productNum_http == count)) {
                currentPage_http++;
                Map<String, String> priceParams = new HashMap<String, String>();
                Map<String, String> commentParams = new HashMap<String, String>();
                Map<String, String> shopParams = new HashMap<String, String>();
                priceParams.put("skuIds", priceBuffer.toString().substring(0, priceBuffer.toString().length() - 1));
                priceParams.put("pdtk", "6Uoh0EB1n439twnPRk%2BuVPXYpSBHK514cL1ZjNJwSrDICNJ0w0eX9YcFSFBxians");
                priceParams.put("pduid", "480222635");
                commentParams.put("referenceIds", commentBuffer.toString().substring(0, commentBuffer.toString().length() - 1));
                shopParams.put("pidList", shopBuffer.toString().substring(0, shopBuffer.toString().length() - 1));

                productFutureList.add(productPool.submit(new ProductCallable(priceParams, commentParams, shopParams)));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                priceBuffer = new StringBuffer();
                commentBuffer = new StringBuffer();
                shopBuffer = new StringBuffer();
            }
        }


        for (Future<Map<String, Object>> future : productFutureList) {
            try {
                Map<String, Object> map = future.get();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    if (key.contains(ProductCallable.PRICE_FLAG)) {
                        priceMap.put(key.replace(ProductCallable.PRICE_FLAG, ""), (Price) entry.getValue());
                    } else if (key.contains(ProductCallable.COMMENTS_FLAG)) {
                        commentMap.put(key.replace(ProductCallable.COMMENTS_FLAG, ""), (Comment) entry.getValue());
                    } else if (key.contains(ProductCallable.SHOP_FLAG)) {
                        shopMap.put(key.replace(ProductCallable.SHOP_FLAG, ""), (Shop) entry.getValue());
                    } else {
                        errorMap.put(entry.getKey(), entry.getValue().toString());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        stopWatch.stop();
        System.out.println("商品附加属性花费时间----------------------------------- " + stopWatch.getTime());
        System.out.println("价格数量-------------------------------------" + priceMap.size());
        System.out.println("评论数量-------------------------------------" + commentMap.size());
        System.out.println("店铺数量-------------------------------------" + shopMap.size());
        System.out.println("错误数量-------------------------------------" + errorMap.size());

        stopWatch.reset();
        stopWatch.start();

        int productNum_database = 0;
        int currentPage_database = 1;
        int remainder_database = count % 1000;
        int totalPage_database = (remainder_database == 0 ? count / 1000 : (count / 1000) + 1);
        List<Product> productList = new ArrayList<Product>();

        for (Map.Entry<String, Product> productEntry : productMap.entrySet()) {
            productNum_database++;
            Product product = productEntry.getValue();
            String skuId = productEntry.getKey();
            Price price = priceMap.get(skuId);
            product.setPrice(price.getPrice());

            Comment comment = commentMap.get(skuId);
            product.setGoodCount(comment.getGoodCount());
            product.setGeneralCount(comment.getGeneralCount());
            product.setPoorCount(comment.getPoorCount());

            Shop shop = shopMap.get(skuId);
            product.setShopId(shop.getShopId());
            product.setShopName(shop.getShopName());
            product.setIsSelfSupport(shop.getIsSelfSupport());

            productList.add(product);

            if ((currentPage_database < totalPage_database && productNum_database % 1000 == 0) || (currentPage_database == totalPage_database && productNum_database == count)) {
                currentPage_database++;
                synchronized (ProductPage.class) {
                    productDao.insertProduct(productList);
                    sqlSession.commit();
//                        System.out.println(productList);
                }
            }
        }
    }

    public void process(Page page) {
        Date date = new Date();
        Html html = page.getHtml();
        List<Product> productList = new ArrayList<Product>();
        /*StringBuffer priceBuffer = new StringBuffer();
        StringBuffer commentBuffer = new StringBuffer();
        StringBuffer shopBuffer = new StringBuffer();
        Map<String, String> priceParams = new HashMap<String, String>();
        Map<String, String> commentParams = new HashMap<String, String>();
        Map<String, String> shopParams = new HashMap<String, String>();
        for (int i = 0; i < 60; i++) {
            String skuId = html.xpath("//div[@id='J_searchWrap']/div[@id='J_container']/div[@id='J_main']/div[@class='m-list']/div[@class='ml-wrap']/div[@id='plist']/ul[@class='gl-warp clearfix']/li[" + (i + 1) + "]/div[@class='gl-i-wrap j-sku-item']/@data-sku").toString();
            if (StringUtils.isNotEmpty(skuId)) {
                priceBuffer.append("J_").append(skuId).append(",");
                commentBuffer.append(skuId).append(",");
                shopBuffer.append(skuId).append(",");
            }
        }
        priceParams.put("skuIds", priceBuffer.toString().substring(0, priceBuffer.toString().length() - 1));
        commentParams.put("referenceIds", commentBuffer.toString().substring(0, commentBuffer.toString().length() - 1));
        shopParams.put("pidList", shopBuffer.toString().substring(0, shopBuffer.toString().length() - 1));
        String priceResult = HttpClientUtils.doGet(PRICE_URL, priceParams, 0, null);
        JSONArray priceJSONArray = JSONArray.parseArray(priceResult);
        String commentResult = HttpClientUtils.doGet(COMMENTS_URL, commentParams, 0, null);
        JSONArray commentJSONArray = JSONObject.parseObject(commentResult).getJSONArray("CommentsCount");
        String shopResult = HttpClientUtils.doGet(SHOP_URL, shopParams, 0, "UTF-8");
        JSONArray shopJSONArray = JSONArray.parseArray(shopResult.substring(5, shopResult.length() - 2));

        Map<String, Price> priceMap = new HashMap<String, Price>();
        Map<String, Comment> commentMap = new HashMap<String, Comment>();
        Map<String, Shop> shopMap = new HashMap<String, Shop>();
        for (int i = 0; i < priceJSONArray.size(); i++) {
            JSONObject priceJSONObject = priceJSONArray.getJSONObject(i);
            Set<Map.Entry<String, Object>> priceEntry = priceJSONObject.entrySet();
            Price price=new Price();
            for (Map.Entry<String, Object> entry : priceEntry) {
                String key = entry.getKey();
                if (key.equals("id")) {
                    price.setSkuId(entry.getValue().toString().replace("J_", ""));
                }
                if (key.equals("p")) {
                    price.setPrice(Double.valueOf(entry.getValue().toString()));
                }
            }
            priceMap.put(price.getSkuId(), price);
        }

        for (int i = 0; i < commentJSONArray.size(); i++) {
            JSONObject commentJSONObject = commentJSONArray.getJSONObject(i);
            Set<Map.Entry<String, Object>> commentEntry = commentJSONObject.entrySet();
            Comment comment=new Comment();
            for (Map.Entry<String, Object> entry : commentEntry) {
                String key = entry.getKey();
                if (key.equals("SkuId")) {
                    comment.setSkuId(entry.getValue().toString());
                }
                if (key.equals("GoodCount")) {
                    comment.setGoodCount(Integer.valueOf(entry.getValue().toString()));
                }
                if (key.equals("GeneralCount")) {
                    comment.setGeneralCount(Integer.valueOf(entry.getValue().toString()));
                }if (key.equals("PoorCount")) {
                    comment.setPoorCount(Integer.valueOf(entry.getValue().toString()));
                }
            }
            commentMap.put(comment.getSkuId(), comment);
        }

        for (int i = 0; i < shopJSONArray.size(); i++) {
            JSONObject shopJSONObject = shopJSONArray.getJSONObject(i);
            Set<Map.Entry<String, Object>> shopEntry = shopJSONObject.entrySet();
            Shop shop=new Shop();
            for (Map.Entry<String, Object> entry : shopEntry) {
                String key = entry.getKey();
                if (key.equals("pid")) {
                    shop.setSkuId(entry.getValue().toString());
                }
                if (key.equals("shopId")) {
                    shop.setShopId(entry.getValue().toString());
                }
                if (key.equals("seller")) {
                    shop.setShopName(entry.getValue().toString());
                }if (key.equals("venderId")) {
                    shop.setVenderId(entry.getValue().toString());
                }
            }
            if(shop.getShopId().equals(shop.getVenderId())){
                shop.setIsSelfSupport(1);
            }
            shopMap.put(shop.getSkuId(), shop);
        }*/


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

                /*Price price=priceMap.get(skuId);
                product.setPrice(price.getPrice());

                Comment comment=commentMap.get(skuId);
                product.setGoodCount(comment.getGoodCount());
                product.setGeneralCount(comment.getGeneralCount());
                product.setPoorCount(comment.getPoorCount());

                Shop shop=shopMap.get(skuId);
                product.setShopId(shop.getShopId());
                product.setShopName(shop.getShopName());
                product.setIsSelfSupport(shop.getIsSelfSupport());*/

                try {
                    productMap.put(skuId, product);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                productList.add(product);
            }

        }
        /*synchronized (this) {
            productDao.insertProduct(productList);
            sqlSession.commit();
//                        System.out.println(productList);
        }*/
    }

    public Site getSite() {
        return site;
    }
}