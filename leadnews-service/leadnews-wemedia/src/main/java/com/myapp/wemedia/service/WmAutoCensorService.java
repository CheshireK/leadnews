package com.myapp.wemedia.service;

public interface WmAutoCensorService {
    /**
     * 自媒体文章审核
     * @param newsId 自媒体文章id
     */
    void autoCensorWmNews(Integer newsId);
}
