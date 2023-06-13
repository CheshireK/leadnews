package com.myapp.model.behavior.constant;

public class BehaviorConstant {
    /**
     * 类型：文章
     */
    public final static Short TYPE_ARTICLE = 0;
    /**
     * 类型：动态
     */
    public final static Short TYPE_MOMENT = 1;

    public final static String LIKE_BEHAVIOR = "like_article_";
    public final static String UN_LIKE_BEHAVIOR = "unlike_article_";
    public final static String COLLECTION_BEHAVIOR = "collection_article_";
    public final static String VIEW_BEHAVIOR = "view_article_";

    /**
     * 取消点赞
     */
    public final static Short OPERATION_UN_LIKE = 1;
    /**
     * 点赞
     */
    public final static Short OPERATION_LIKE = 0;

    /**
     * 取消收藏
     */
    public final static Short OPERATION_UN_COLLECT = 1;
    /**
     * 收藏
     */
    public final static Short OPERATION_COLLECT = 0;

}
