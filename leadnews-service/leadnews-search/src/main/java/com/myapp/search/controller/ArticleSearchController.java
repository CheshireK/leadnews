package com.myapp.search.controller;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.search.dto.UserSearchDto;
import com.myapp.search.service.ApArticleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article/search/")
public class ArticleSearchController {
    @Autowired
    private ApArticleSearchService apArticleSearchService;

    @PostMapping("/search")
    public ResponseResult articleSearch(@RequestBody UserSearchDto searchDto){
        return apArticleSearchService.search(searchDto);
    }
}
