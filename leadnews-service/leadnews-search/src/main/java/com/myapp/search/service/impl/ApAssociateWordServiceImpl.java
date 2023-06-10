package com.myapp.search.service.impl;

import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.search.dto.UserSearchDto;
import com.myapp.model.search.pojo.ApAssociateWord;
import com.myapp.search.service.ApAssociateWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ApAssociateWordServiceImpl implements ApAssociateWordService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 加载用户搜索关联词
     * @param searchDto 用户搜索关键词
     * @return 关联词列表
     */
    @Override
    public ResponseResult listAssociateWord(UserSearchDto searchDto) {
        String keyword = searchDto.getSearchWords();
        if (StringUtils.isEmpty(keyword)){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        Query query = Query.query(Criteria.where("associateWords").regex(".*?\\" + keyword + ".*"));
        List<ApAssociateWord> list = mongoTemplate.find(query, ApAssociateWord.class);
        return ResponseResult.okResult(list);
    }

    @Override
    public void saveAssociateWordList(List<ApAssociateWord> wordList) {
        if (CollectionUtils.isEmpty(wordList)){
            return;
        }
        mongoTemplate.insertAll(wordList);
    }
}
