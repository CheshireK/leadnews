package com.myapp.article.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.myapp.article.mapper.ApArticleContentMapper;
import com.myapp.model.article.pojo.ApArticle;
import com.myapp.model.article.pojo.ApArticleContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApArticleFreemarkerServiceTest {

    @Autowired
    private ApArticleFreemarkerService freemarkerService;

    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private ApArticleContentMapper contentMapper;

    @Test
    void buildApArticleToMinio() {
        List<ApArticle> list = apArticleService.list();
        List<ApArticleContent> contentList = contentMapper.selectList(new LambdaUpdateWrapper<>());
        for (ApArticle article : list) {
            ApArticleContent content = contentList.stream().filter(item -> item.getArticleId().equals(article.getId()))
                    .limit(1)
                    .collect(Collectors.toList())
                    .get(0);
            freemarkerService.buildApArticleToMinio(article, content.getContent());
        }

    }
}