package com.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.bean.Price;
import com.demo.common.HttpClientUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 价格线程实现类
 *
 * @author chenzhongwei
 * @create 2017-03-18 16:53
 **/
public class PriceCallable implements Callable{

    private static final String PRICE_URL = "https://p.3.cn/prices/mgets";

    private Map<String, String> priceParams;

    public PriceCallable(Map<String, String> priceParams) {
        this.priceParams = priceParams;
    }

    public Object call() throws Exception {
        String priceResult = HttpClientUtils.sendGet(HttpClientUtils.getRealUrl(PRICE_URL, priceParams));
        JSONArray priceJSONArray = JSONArray.parseArray(priceResult);
        ConcurrentHashMap<String, Price> priceMap = new ConcurrentHashMap<String, Price>();
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
            priceMap.put(price.getSkuId(), price);
        }
        System.out.println("价格-------------------------- " + priceMap);
        return priceMap;
    }
}