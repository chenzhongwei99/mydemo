package com.demo.common;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import java.util.Map;

/**
 * http请求工具类
 *
 * @author chenzhongwei
 * @create 2017-03-17 10:12
 **/
public class HttpClientUtils {

    public static HttpClient httpClient = new HttpClient();
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * httpclient get请求
     *
     * @param url     请求的url
     * @param params  请求的参数
     * @param timeOut 超时时间
     * @param charset 字符集
     * @return 返回请求返回的数据
     * @author chenzhongwei
     */
    public static String doGet(String url, Map<String, String> params, Integer timeOut, String charset) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        String resultUrl;
        String result = null;
        if (params == null || params.size() == 0) {
            resultUrl = url;
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            for (Map.Entry<String, String> map : params.entrySet()) {
                stringBuffer.append(map.getKey()).append("=").append(map.getValue());
            }
            resultUrl = url + "?" + stringBuffer.toString();
        }
        synchronized (HttpClientUtils.class) {
            GetMethod getMethod = new GetMethod(resultUrl);
            try {
                if (null != timeOut && timeOut != 0) {
                    httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeOut);
                }
                int statusCode = httpClient.executeMethod(getMethod);
                if (statusCode == HttpStatus.SC_OK) {
                    byte[] responseBody = getMethod.getResponseBody();
                    if (StringUtils.isEmpty(charset)) {
                        result = new String(responseBody);
                    } else {
                        result = new String(responseBody, CHARSET_UTF8);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getMethod.releaseConnection();
            }
        }
        return result;

    }
}