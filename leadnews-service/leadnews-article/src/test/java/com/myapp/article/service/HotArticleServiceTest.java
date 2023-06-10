package com.myapp.article.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class HotArticleServiceTest {
    @Autowired
    private HotArticleService hotArticleService;

    @Test
    void processHotArticle() {
        System.out.println(log.getClass());
        hotArticleService.processHotArticle();
    }
}