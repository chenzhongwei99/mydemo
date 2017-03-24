package com.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.bean.Comment;
import com.demo.bean.Price;
import com.demo.bean.Shop;
import com.demo.common.HttpClientUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 商品的辅助信息线程池
 *
 * @author chenzhongwei
 * @create 2017-03-23 10:07
 **/
public class ProductCallable implements Callable {

    private static final String PRICE_URL = "https://p.3.cn/prices/mgets";

    private static final String COMMENTS_URL = "https://club.jd.com/comment/productCommentSummaries.action";

    private static final String SHOP_URL = "https://chat1.jd.com/api/checkChat";

    public static final String PRICE_FLAG = "PRICE_";

    public static final String COMMENTS_FLAG = "COMMENTS_";

    public static final String SHOP_FLAG = "SHOP_";

    private Map<String, String> priceParams;

    private Map<String, String> commentParams;

    private Map<String, String> shopParams;

    private static final ConcurrentHashMap<String, Object> returnMap = new ConcurrentHashMap<String, Object>();

    private static final ConcurrentHashMap<String, Object> errorMap = new ConcurrentHashMap<String, Object>();

    public ProductCallable(Map<String, String> priceParams, Map<String, String> commentParams, Map<String, String> shopParams) {
        this.priceParams = priceParams;
        this.commentParams = commentParams;
        this.shopParams = shopParams;
    }

    public Object call() throws Exception {
        try {
            String priceResult = HttpClientUtils.sendGet(HttpClientUtils.getRealUrl(PRICE_URL, priceParams));
            JSONArray priceJSONArray = JSONArray.parseArray(priceResult);
            Map<String, Price>priceMap = new HashMap<String, Price>();
            for (int i = 0; i < priceJSONArray.size(); i++) {
                JSONObject priceJSONObject = priceJSONArray.getJSONObject(i);
                Set<Map.Entry<String, Object>> priceEntry = priceJSONObject.entrySet();
                Price price = new Price();
                for (Map.Entry<String, Object> entry : priceEntry) {
                    String key = entry.getKey();
                    if (key.equals("id")) {
                        price.setSkuId(entry.getValue().toString().replace("J_", ""));
                    }
                    if (key.equals("p")) {
                        price.setPrice(Double.valueOf(entry.getValue().toString()));
                    }
                }
                priceMap.put(PRICE_FLAG + price.getSkuId(), price);
            }
            System.out.println("价格-------------------------- " + priceMap);
            returnMap.putAll(priceMap);
        } catch (Exception e) {
            errorMap.put(priceParams.toString(), PRICE_URL);
            e.printStackTrace();
        }


        try{
            String commentResult = HttpClientUtils.sendGet(HttpClientUtils.getRealUrl(COMMENTS_URL, commentParams));
            JSONArray commentJSONArray = JSONObject.parseObject(commentResult).getJSONArray("CommentsCount");
            Map<String, Comment> commentMap = new HashMap<String, Comment>();
            for (int i = 0; i < commentJSONArray.size(); i++) {
                JSONObject commentJSONObject = commentJSONArray.getJSONObject(i);
                Set<Map.Entry<String, Object>> commentEntry = commentJSONObject.entrySet();
                Comment comment = new Comment();
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
                    }
                    if (key.equals("PoorCount")) {
                        comment.setPoorCount(Integer.valueOf(entry.getValue().toString()));
                    }
                }
                commentMap.put(COMMENTS_FLAG + comment.getSkuId(), comment);
            }
            System.out.println("评论-------------------------- " + commentMap);
            returnMap.putAll(commentMap);
        }catch (Exception e){
            errorMap.put(commentParams.toString(), COMMENTS_URL);
            e.printStackTrace();
        }

        try {
            String shopResult = HttpClientUtils.sendGet(HttpClientUtils.getRealUrl(SHOP_URL, shopParams));
            JSONArray shopJSONArray = JSONArray.parseArray(shopResult.substring(5, shopResult.length() - 2));
            Map<String, Shop> shopMap = new HashMap<String, Shop>();
            for (int i = 0; i < shopJSONArray.size(); i++) {
                JSONObject shopJSONObject = shopJSONArray.getJSONObject(i);
                Set<Map.Entry<String, Object>> shopEntry = shopJSONObject.entrySet();
                Shop shop = new Shop();
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
                    }
                    if (key.equals("venderId")) {
                        shop.setVenderId(entry.getValue().toString());
                    }
                }
                if (shop.getShopId().equals(shop.getVenderId())) {
                    shop.setIsSelfSupport(1);
                }
                shopMap.put(SHOP_FLAG + shop.getSkuId(), shop);
            }
            System.out.println("店铺-------------------------- " + shopMap);
            returnMap.putAll(shopMap);
        } catch (Exception e) {
            e.printStackTrace();
            errorMap.put(shopParams.toString(), SHOP_URL);
        }

        if(errorMap!=null&&errorMap.size()>0){
            returnMap.putAll(errorMap);
        }
        return returnMap;
    }
}