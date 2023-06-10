package com.myapp.article.service;

import com.myapp.model.article.pojo.ApArticle;

public interface ApArticleFreemarkerService {
    /**
     * 生成静态文件上传到minIO中
     * 发送异步消息，通知es添加文章索引
     * @param article
     * @param content
     */
    public void buildApArticleToMinio(ApArticle article, String content);
}
