package com.myapp.behavior.service;

import com.myapp.model.behavior.dto.CollectionBehaviorDto;
import com.myapp.model.behavior.dto.LikeBehaviorDto;
import com.myapp.model.behavior.dto.ReadBehaviorDto;
import com.myapp.model.behavior.dto.UnLikeBehaviorDto;
import com.myapp.model.common.dto.ResponseResult;

public interface UserBehaviorService {

    /**
     * 用户点赞
     * @param dto 点赞行为
     * @return 成功响应
     */
    ResponseResult likeArticle(LikeBehaviorDto dto);

    /**
     * 用户阅读
     * @param dto 阅读行为
     * @return 成功响应
     */
    ResponseResult readArticle(ReadBehaviorDto dto);
    /**
     * 用户收藏
     * @param dto 收藏行为
     * @return 成功响应
     */
    ResponseResult collectArticle(CollectionBehaviorDto dto);

    /**
     * 用户不喜欢文章
     * @param dto 不喜欢行为
     *   {
     * 	    "articleId": 0,
     * 	    "type": 0
     * }
     * @return 响应示例
     * {
     *     "code": 0,
     * 	    "data": {},
     * 	    "errorMessage": "",
     * 	    "host": ""
     * }
     */
    ResponseResult unlikeArticle(UnLikeBehaviorDto dto);
}
