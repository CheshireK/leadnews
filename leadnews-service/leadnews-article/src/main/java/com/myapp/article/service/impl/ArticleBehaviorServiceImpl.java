package com.myapp.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.myapp.article.mapper.ApCollectionMapper;
import com.myapp.article.service.ArticleBehaviorService;
import com.myapp.common.redis.CacheService;
import com.myapp.model.article.dto.ApCollection;
import com.myapp.model.article.dto.ArticleInfoDto;
import com.myapp.model.behavior.constant.BehaviorConstant;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.user.pojo.ApUser;
import com.myapp.util.thread.ApThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class ArticleBehaviorServiceImpl implements ArticleBehaviorService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ApCollectionMapper apCollectionMapper;

    /**
     * 数据回显
     *
     * @param dto {
     *            "articleId": 0,
     *            "authorId": 0
     *            }
     * @return {
     * "host":null,
     * "code":200,
     * "errorMessage":"操作成功",
     * "data":{
     * "islike":false,
     * "isunlike":false,
     * "iscollection":false,
     * "isfollow":false
     * }
     * }
     */
    @Override
    public ResponseResult loadBehavior(ArticleInfoDto dto) {
        if (dto==null || dto.getArticleId() ==null || dto.getAuthorId()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUser user = ApThreadLocalUtil.getUser();
        if (user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        Integer userId = user.getId();
        Integer authorId = dto.getAuthorId();
        Long articleId = dto.getArticleId();
        Map<String, Boolean> responseMap = new HashMap<>();
        responseMap.put("isLike", false);
        responseMap.put("isunlike", false);
        responseMap.put("iscollection", false);
        responseMap.put("isfollow", false);


        String like = (String) cacheService.hGet(BehaviorConstant.LIKE_BEHAVIOR + articleId, userId.toString());
        String unlike = (String) cacheService.hGet(BehaviorConstant.UN_LIKE_BEHAVIOR + articleId, userId.toString());

        ApCollection apCollection = apCollectionMapper.selectOne(new LambdaUpdateWrapper<ApCollection>()
                .eq(ApCollection::getEntryId, userId)
                .eq(ApCollection::getArticleId, articleId));
        if (StringUtils.hasLength(like) ){
            responseMap.put("islike", true);
        }
        if(StringUtils.hasLength(unlike)){
            responseMap.put("unlike", true);
        }
        if (apCollection!=null){
            responseMap.put("iscollection", true);
        }
        return ResponseResult.okResult(responseMap);
    }
}
