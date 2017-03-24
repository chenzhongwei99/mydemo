package com.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.bean.Comment;
import com.demo.common.HttpClientUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 评论线程实现类
 *
 * @author chenzhongwei
 * @create 2017-03-18 16:53
 **/
public class CommentCallable implements Callable {

    private static final String COMMENTS_URL = "https://club.jd.com/comment/productCommentSummaries.action";

    private Map<String, String> commentParams;

    public CommentCallable(Map<String, String> commentParams) {
        this.commentParams = commentParams;
    }

    public Object call() throws Exception {
        String commentResult = HttpClientUtils.sendGet(HttpClientUtils.getRealUrl(COMMENTS_URL, commentParams));
        JSONArray commentJSONArray = JSONObject.parseObject(commentResult).getJSONArray("CommentsCount");
        ConcurrentHashMap<String, Comment> commentMap = new ConcurrentHashMap<String, Comment>();
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
            commentMap.put(comment.getSkuId(), comment);
        }
        System.out.println("评论-------------------------- " + commentMap);
        return commentMap;
    }
}