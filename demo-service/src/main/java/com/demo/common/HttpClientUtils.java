package com.demo.common;


import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;



public class HttpClientUtils {

    private static final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();

    static {
        poolingHttpClientConnectionManager.setMaxTotal(5000);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(1500);
    }

    private static final HttpHost proxy=new HttpHost("221.180.170.104", 80);

    private static HttpClientBuilder httpClientBuilder=HttpClients.custom();

    private static CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager).build();

//    private static final RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();

    private static final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();



    public static String sendGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse closeableHttpResponse = null;
        String result = null;
        try {
            closeableHttpResponse = httpClient.execute(httpGet);
            if(closeableHttpResponse!=null){
                HttpEntity httpEntity = closeableHttpResponse.getEntity();
                if(httpEntity!=null){
                    result = EntityUtils.toString(httpEntity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(closeableHttpResponse!=null){
                    closeableHttpResponse.close();
                }
            } catch (IOException e) {
                System.out.println("httpget----------------------------url:"+url+" error:"+e.getMessage());
            }
        }
        return result;
    }

    public static String getRealUrl(String url, Map<String, String> params) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        String resultUrl;
        if (params == null || params.size() == 0) {
            resultUrl = url;
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            int i=0;
            for (Map.Entry<String, String> map : params.entrySet()) {
                if(i==0){
                    stringBuffer.append(map.getKey()).append("=").append(map.getValue());
                }else{
                    stringBuffer.append("&").append(map.getKey()).append("=").append(map.getValue());
                }
                i++;
            }
            resultUrl = url + "?" + stringBuffer.toString();
        }
        return resultUrl;
    }
}
