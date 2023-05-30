package com.myapp.article.controller.v1;

import com.myapp.article.service.ApArticleService;
import com.myapp.model.article.dto.ArticleHomeDto;
import com.myapp.model.common.dto.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.myapp.model.common.constant.ArticleConstant.LOADTYPE_LOAD_MORE;
import static com.myapp.model.common.constant.ArticleConstant.LOADTYPE_LOAD_NEW;

@RestController
@Slf4j
@RequestMapping("/api/v1/article")
@Api(value = "app端用户查询文章", tags = "ap_article")
public class ApArticleController {

    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/load")
    @ApiOperation(value = "查询更多文章，以用户加载的文章的最后的文章的时间为基准加载时间更早的文章")
    public ResponseResult load(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(LOADTYPE_LOAD_MORE, articleHomeDto);
    }

    @PostMapping("/loadmore")
    @ApiOperation(value = "查询更多文章，与/load请求功能相同")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(LOADTYPE_LOAD_MORE, articleHomeDto);
    }

    @PostMapping("/loadnew")
    @ApiOperation(value = "查询最新文章")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(LOADTYPE_LOAD_NEW, articleHomeDto);
    }
}
