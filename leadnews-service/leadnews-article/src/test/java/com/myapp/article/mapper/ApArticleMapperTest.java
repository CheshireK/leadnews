package com.myapp.article.mapper;

import com.myapp.model.article.pojo.ApArticle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@SpringBootTest
class ApArticleMapperTest {

    @Autowired
    private ApArticleMapper articleMapper;

    @Test
    void loadArticleListByDate() {

        LocalDateTime time = LocalDateTime.of(2021, 4, 1, 0, 0);
        Date date = new Date(time.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        System.out.println(date);
        List<ApArticle> apArticles = articleMapper.loadArticleListByDate(date, 30);

        System.out.println(apArticles.size());
    }
}