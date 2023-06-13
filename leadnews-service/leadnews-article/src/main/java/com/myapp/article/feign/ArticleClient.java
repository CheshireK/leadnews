package com.myapp.article.feign;

import com.myapp.api.article.IArticleClient;
import com.myapp.article.service.ApArticleService;
import com.myapp.model.article.dto.ArticleDto;
import com.myapp.model.behavior.dto.CollectionBehaviorDto;
import com.myapp.model.common.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService apArticleService;

    @Override
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveOrUpdateArticle(dto);
    }

    @Override
    @PostMapping("/api/v1/article/collect")
    public ResponseResult collectArticle(CollectionBehaviorDto dto) {
        return apArticleService.collectArticle(dto);
    }

}