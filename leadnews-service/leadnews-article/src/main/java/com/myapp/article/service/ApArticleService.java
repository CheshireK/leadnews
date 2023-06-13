package com.myapp.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myapp.model.article.dto.ArticleDto;
import com.myapp.model.article.dto.ArticleHomeDto;
import com.myapp.model.article.pojo.ApArticle;
import com.myapp.model.behavior.dto.CollectionBehaviorDto;
import com.myapp.model.common.dto.ResponseResult;

public interface ApArticleService  extends IService<ApArticle> {
    /**
     * 根据参数加载文章列表
     * @param loadType 1为加载更多  2为加载最新
     * @param articleHomeDto 主页参数
     * @return 成功响应
     */
    ResponseResult load(short loadType, ArticleHomeDto articleHomeDto);

    /**
     * 保存文章
     * @param dto 文章dto
     * @return 成功响应
     */
    ResponseResult saveOrUpdateArticle(ArticleDto dto);

    /**
     * 上架或下架文章
     * @param articleId 文章id
     * @return 成功响应
     */
    ResponseResult upOrDownArticle(Long articleId);

    /**
     * 删除文章
     * @param articleId 文章id
     * @return 成功响应
     */
    ResponseResult deleteArticle(Long articleId);

    /**
     * 收藏文章
     * @param dto 收藏行为
     * @return 成功响应
     */
    ResponseResult collectArticle(CollectionBehaviorDto dto);
}
