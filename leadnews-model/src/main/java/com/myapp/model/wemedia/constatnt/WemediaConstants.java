package com.myapp.model.wemedia.constatnt;

public class WemediaConstants {

    public static final Short COLLECT_MATERIAL = 1;// 收藏

    public static final Short CANCEL_COLLECT_MATERIAL = 0;// 取消收藏

    public static final String WM_NEWS_TYPE_IMAGE = "image";

    public static final Short WM_NEWS_NONE_IMAGE = 0;
    public static final Short WM_NEWS_SINGLE_IMAGE = 1;
    public static final Short WM_NEWS_MANY_IMAGE = 3;
    public static final Short WM_NEWS_TYPE_AUTO = -1;
    // 文章上架
    public static final Short WM_NEWS_ENABLE = 1;
    // 文章下架
    public static final Short WM_NEWS_DISABLE = 0;

    public static final Short WM_CONTENT_REFERENCE = 0;
    public static final Short WM_COVER_REFERENCE = 1;
    /**
     * 状态：草稿
     */
    public static final Short WM_STATUS_DRAFT = 0;
    /**
     * 状态：提交（待审核）
     */
    public static final Short WM_STATUS_SUBMIT = 1;
    /**
     * 状态：审核失败
     */
    public static final Short WM_STATUS_CENSOR_FAIL = 2;
    /**
     * 状态：人工审核
     */
    public static final Short WM_STATUS_CENSOR_REVIEW = 3;
    /**
     * 状态：人工审核通过
     */
    public static final Short WM_STATUS_CENSOR_REVIEW_SUCCESS = 4;
    /**
     * 状态：审核通过，待发布
     */
    public static final Short WM_STATUS_CENSOR_SUCCESS = 8;
    /**
     * 状态：已发布
     */
    public static final Short WM_STATUS_PUBLISHED = 9;


}