package com.myapp.article.controller.v1;

import com.myapp.article.service.ApArticleService;
import com.myapp.article.service.ArticleBehaviorService;
import com.myapp.model.article.dto.ArticleInfoDto;
import com.myapp.model.behavior.dto.CollectionBehaviorDto;
import com.myapp.model.common.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class UserBehaviorController {

    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private ArticleBehaviorService articleBehaviorService;

    @PostMapping("/collection_behavior")
    public ResponseResult collectBehavior(@RequestBody CollectionBehaviorDto dto){
        return apArticleService.collectArticle(dto);
    }

    @PostMapping("/article/load_article_behavior")
    public ResponseResult loadArticleBehavior(@RequestBody ArticleInfoDto dto){
        return articleBehaviorService.loadBehavior(dto);
    }
}
