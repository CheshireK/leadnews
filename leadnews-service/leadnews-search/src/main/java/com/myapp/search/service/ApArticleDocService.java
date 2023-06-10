package com.myapp.search.service;

public interface ApArticleDocService {
    /**
     * 向es建立app article索引
     * @param articleVoJSON article json字符串
     */
    void saveArticleDoc(String articleVoJSON);
}
