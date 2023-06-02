package com.myapp.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myapp.model.article.dto.ArticleDto;
import com.myapp.model.article.dto.ArticleHomeDto;
import com.myapp.model.article.pojo.ApArticle;
import com.myapp.model.common.dto.ResponseResult;

public interface ApArticleService  extends IService<ApArticle> {
    /**
     * 根据参数加载文章列表
     * @param loadType 1为加载更多  2为加载最新
     * @param articleHomeDto
     * @return
     */
    ResponseResult load(short loadType, ArticleHomeDto articleHomeDto);

    /**
     * 保存文章
     * @param dto
     * @return
     */
    ResponseResult saveOrUpdateArticle(ArticleDto dto);
}
