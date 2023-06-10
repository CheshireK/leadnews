package com.myapp.search.service;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.search.dto.UserSearchDto;
import com.myapp.model.search.pojo.ApAssociateWord;

import java.util.List;

public interface ApAssociateWordService {
    /**
     * 加载用户搜索关联词
     * @param searchDto 用户搜索关键词
     * @return 关联词列表
     */
    ResponseResult listAssociateWord(UserSearchDto searchDto);

    void saveAssociateWordList(List<ApAssociateWord> wordList);
}
