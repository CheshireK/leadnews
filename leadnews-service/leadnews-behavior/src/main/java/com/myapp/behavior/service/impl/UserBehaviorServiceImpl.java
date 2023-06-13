package com.myapp.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.myapp.api.article.IArticleClient;
import com.myapp.behavior.service.UserBehaviorService;
import com.myapp.common.redis.CacheService;
import com.myapp.model.behavior.constant.BehaviorConstant;
import com.myapp.model.behavior.dto.CollectionBehaviorDto;
import com.myapp.model.behavior.dto.LikeBehaviorDto;
import com.myapp.model.behavior.dto.ReadBehaviorDto;
import com.myapp.model.behavior.dto.UnLikeBehaviorDto;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.user.pojo.ApUser;
import com.myapp.util.thread.ApThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class UserBehaviorServiceImpl implements UserBehaviorService {

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult likeArticle(LikeBehaviorDto dto) {
        if (!checkLikeBehaviorDtoParam(dto)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUser user = ApThreadLocalUtil.getUser();
        if (user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        String key = BehaviorConstant.LIKE_BEHAVIOR + dto.getArticleId();
        Integer userId = user.getId();
        // 点赞
        if (dto.getOperation().equals(BehaviorConstant.OPERATION_LIKE)){
            if (cacheService.hGet(key, userId.toString())!=null){
                log.error("用户已经点赞,userid={}, articleId={}", userId, dto.getArticleId());
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已点赞");
            }
            log.info("用户点赞,userid={}, articleId={}", userId, dto.getArticleId());
            cacheService.hPut(key, userId.toString(), JSON.toJSONString(dto));
        }
        // 取消点赞
        else {
            log.info("用户取消点赞,userid={}, articleId={}", userId, dto.getArticleId());
            cacheService.hDelete(key, userId.toString());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 用户阅读
     *
     * @param dto 阅读行为
     * @return 成功响应
     */
    @Override
    public ResponseResult readArticle(ReadBehaviorDto dto) {
        if(!checkReadBehaviorDtoParam(dto)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID); 
        }
        ApUser user = ApThreadLocalUtil.getUser();
        if (user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        String key = BehaviorConstant.VIEW_BEHAVIOR + dto.getArticleId();
        String userId = user.getId().toString();
        String readJSON = (String) cacheService.hGet(key, userId);
        if (!StringUtils.isEmpty(readJSON)){
            log.debug("用户阅读不为空,json={}", readJSON);
            ReadBehaviorDto readBehaviorDto = JSON.parseObject(readJSON, ReadBehaviorDto.class);
            readBehaviorDto.setCount(readBehaviorDto.getCount() + 1);
        }else {
            cacheService.hPut(key, userId, JSON.toJSONString(dto));
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Autowired
    private IArticleClient articleClient;
    /**
     * 用户收藏
     * @param dto 收藏行为
     * @return 成功响应
     */
    @Override
    public ResponseResult collectArticle(CollectionBehaviorDto dto) {
        return articleClient.collectArticle(dto);
    }

    /**
     * 用户不喜欢文章
     *
     * @param dto 不喜欢行为
     *            {
     *            "articleId": 0,
     *            "type": 0
     *            }
     * @return 响应示例
     * {
     * "code": 0,
     * "data": {},
     * "errorMessage": "",
     * "host": ""
     * }
     */
    @Override
    public ResponseResult unlikeArticle(UnLikeBehaviorDto dto) {
        if (dto == null || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (dto.getType() ==null || dto.getType() < 0 || dto.getType() > 1){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "Type参数错误");
        }
        ApUser user = ApThreadLocalUtil.getUser();
        if (user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        Long articleId = dto.getArticleId();
        Integer userId = user.getId();
        String key = BehaviorConstant.UN_LIKE_BEHAVIOR + articleId;
        // 确认不喜欢
        if (dto.getType().equals((short) 0)){
            // 如果用户点击了喜欢，则删除喜欢信息
            if (cacheService.hGet(BehaviorConstant.LIKE_BEHAVIOR +articleId, userId.toString())!=null){
                log.info("删除用户喜欢信息：userid={}, articleId={}", userId, articleId);
                cacheService.hDelete(BehaviorConstant.LIKE_BEHAVIOR +articleId, userId.toString());
            }

            //
            if (cacheService.hGet(key, userId.toString())!=null){
                log.error("不喜欢数据已存在,userid={}, articleId={}", userId, articleId);
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已点赞");
            }
            log.info("用户不喜欢,userid={}, articleId={}", userId, articleId);
            cacheService.hPut(key, userId.toString(), JSON.toJSONString(dto));
        }
        // 取消不喜欢
        else {
            log.info("用户取消不喜欢,userid={}, articleId={}", userId, articleId);
            cacheService.hDelete(key, userId.toString());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private boolean checkReadBehaviorDtoParam(ReadBehaviorDto dto) {
        if (dto == null || dto.getArticleId() == null)
            return false;
        if (dto.getCount() == null || dto.getCount()!=1){
            dto.setCount(1);
        }
        return true;
    }


    private boolean checkLikeBehaviorDtoParam(LikeBehaviorDto dto) {
        if (dto == null || dto.getArticleId() == null)
            return false;
        if (dto.getType() == null || dto.getType() < 0 || dto.getType() > 2)
            return false;
        if (dto.getOperation() == null || dto.getOperation() < 0 || dto.getOperation() > 1)
            return false;
        return true;
    }
}
