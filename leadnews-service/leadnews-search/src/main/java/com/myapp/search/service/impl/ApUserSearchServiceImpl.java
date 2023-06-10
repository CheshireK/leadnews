package com.myapp.search.service.impl;

import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.search.dto.HistorySearchDto;
import com.myapp.model.search.pojo.ApUserSearch;
import com.myapp.model.user.pojo.ApUser;
import com.myapp.search.service.ApUserSearchService;
import com.myapp.util.thread.ApThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ApUserSearchServiceImpl implements ApUserSearchService {
    public static final String USER_ID = "userId";
    public static final String CREATED_TIME = "createdTime";
    public static final String KEYWORD = "keyword";
    public static final String ID = "id";
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存用户搜索记录
     * @param word   搜索关键词
     * @param userId 用户id
     */
    @Override
    @Async
    public void save(String word, Integer userId) {
        if (StringUtils.isEmpty(word) || userId == null) {
            return;
        }

        // 查询搜索记录
        Query query = new Query(Criteria.where(USER_ID).is(userId)
                .and(KEYWORD).is(word));
        ApUserSearch search = mongoTemplate.findOne(query, ApUserSearch.class);
        // 判断是否存在记录
        // 若存在，更新至最新时间
        if (search != null) {
            UpdateDefinition updateDefinition = new Update().set(CREATED_TIME, new Date());
            mongoTemplate.updateFirst(query, updateDefinition, ApUserSearch.class);
            return;
        }

        // 插入新记录，判断记录数量是否超过10
        Query query2 = new Query(Criteria.where(USER_ID).is(userId))
                .with(Sort.by(Sort.Direction.ASC, CREATED_TIME));
        List<ApUserSearch> searchList = mongoTemplate.find(query2, ApUserSearch.class);
        // 若超过10条则替换时间最久的一条数据
        if (searchList.size() >= 10){
            mongoTemplate.remove(searchList.get(0));
        }

        ApUserSearch apUserSearch = new ApUserSearch();
        apUserSearch.setKeyword(word);
        apUserSearch.setUserId(userId);
        apUserSearch.setCreatedTime(new Date());
        mongoTemplate.save(apUserSearch);
        log.info("保存用户搜索记录userid={}, word={}", userId, word);
    }

    /**
     * 加载用户搜索历史
     * 按创建时间的降序排列
     * @return 用户搜索历史
     */
    @Override
    public ResponseResult loadUserSearchHistory() {
        ApUser user = ApThreadLocalUtil.getUser();
        if (user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        Query query = new Query(Criteria.where(USER_ID).is(user.getId()))
                .with(Sort.by(Sort.Direction.DESC, CREATED_TIME));
        List<ApUserSearch> searchList = mongoTemplate.find(query, ApUserSearch.class);
        return ResponseResult.okResult(searchList);
    }

    /**
     * 删除用户搜索历史
     * @param searchDto 用户搜索历史
     * @return
     */
    @Override
    public ResponseResult deleteUserSearchHistory(HistorySearchDto searchDto) {
        if (searchDto == null || StringUtils.isEmpty(searchDto.getId())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUser user = ApThreadLocalUtil.getUser();
        if (user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        Query query = Query.query(Criteria.where(USER_ID).is(user.getId()).and(ID).is(searchDto.getId()));
        mongoTemplate.remove(query, ApUserSearch.class);
        log.info("删除用户搜索记录userid={}", user.getId());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
