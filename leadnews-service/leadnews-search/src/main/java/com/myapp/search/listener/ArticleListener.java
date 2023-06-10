package com.myapp.search.listener;

import com.myapp.model.common.constant.ArticleMessageConstant;
import com.myapp.search.service.ApArticleDocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ArticleListener {

    @Autowired
    private ApArticleDocService articleDocService;

    @KafkaListener(topics = ArticleMessageConstant.ARTICLE_ES_SYNC_TOPIC)
    public void onMessage(String articleVoJSON){
        articleDocService.saveArticleDoc(articleVoJSON);
    }
}
