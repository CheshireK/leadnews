package com.myapp.search.service;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.search.dto.UserSearchDto;

public interface ApArticleSearchService {
    /**
     * Elasticsearch app 文章搜索
     * @param dto 查询条件
     * @return 响应
     */
    ResponseResult search(UserSearchDto dto);
}
