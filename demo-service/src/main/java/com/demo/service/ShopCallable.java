package com.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.bean.Shop;
import com.demo.common.HttpClientUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 店铺线程实现类
 *
 * @author chenzhongwei
 * @create 2017-03-18 16:53
 **/
public class ShopCallable implements Callable {

    private static final String SHOP_URL = "https://chat1.jd.com/api/checkChat";

    private Map<String, String> shopParams;

    public ShopCallable(Map<String, String> shopParams) {
        this.shopParams = shopParams;
    }

    public Object call() throws Exception {
        String shopResult = HttpClientUtils.sendGet(HttpClientUtils.getRealUrl(SHOP_URL, shopParams));
        JSONArray shopJSONArray = JSONArray.parseArray(shopResult.substring(5, shopResult.length() - 2));
        ConcurrentHashMap<String, Shop> shopMap = new ConcurrentHashMap<String, Shop>();
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
            shopMap.put(shop.getSkuId(), shop);
        }
        System.out.println("店铺-------------------------- " + shopMap);
        return shopMap;
    }
}