package com.myapp.search.service;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.search.dto.HistorySearchDto;

public interface ApUserSearchService {
    /**
     * 保存用户搜索记录
     * @param word 搜索关键词
     * @param userId 用户id
     */
    public void save(String word, Integer userId);

    /**
     * 加载用户搜索历史
     * 按创建时间的降序排列
     * @return 用户搜索历史
     */
    ResponseResult loadUserSearchHistory();

    /**
     * 删除用户搜索历史
     * @param searchDto 用户搜索历史
     * @return
     */
    ResponseResult deleteUserSearchHistory(HistorySearchDto searchDto);
}
