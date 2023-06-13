package com.myapp.article.service;

import com.myapp.model.article.dto.ArticleInfoDto;
import com.myapp.model.common.dto.ResponseResult;

public interface ArticleBehaviorService {
    /**
     * 数据回显
     * @param dto {
     * 	"articleId": 0,
     * 	"authorId": 0
     * }
     * @return {
     *     "host":null,
     *     "code":200,
     *     "errorMessage":"操作成功",
     *     "data":{
     *         "islike":false,
     *         "isunlike":false,
     *         "iscollection":false,
     *         "isfollow":false
     *     }
     * }
     */
    ResponseResult loadBehavior(ArticleInfoDto dto);
}
