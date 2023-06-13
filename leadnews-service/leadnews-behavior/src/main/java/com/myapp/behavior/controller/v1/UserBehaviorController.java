package com.myapp.behavior.controller.v1;

import com.myapp.behavior.service.UserBehaviorService;
import com.myapp.model.behavior.dto.LikeBehaviorDto;
import com.myapp.model.behavior.dto.ReadBehaviorDto;
import com.myapp.model.behavior.dto.UnLikeBehaviorDto;
import com.myapp.model.common.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;

    /**
     * 用户点赞
     * @param dto 点赞行为
     * @return 成功响应
     */
    @PostMapping("/likes_behavior")
    public ResponseResult likeBehavior(@RequestBody LikeBehaviorDto dto){
        return userBehaviorService.likeArticle(dto);
    }

    /**
     * 用户取消点赞
     * @param dto 点赞行为
     * @return 成功响应
     */
    @PostMapping("/un_likes_behavior")
    public ResponseResult unLikeBehavior(@RequestBody UnLikeBehaviorDto dto){
        return userBehaviorService.unlikeArticle(dto);
    }

    /**
     * 用户阅读
     * @param dto 阅读行为
     * @return 成功响应
     */
    @PostMapping("/read_behavior")
    public ResponseResult readBehavior(@RequestBody ReadBehaviorDto dto){
        return userBehaviorService.readArticle(dto);
    }

    /**
     * 用户收藏
     */
    // @PostMapping("/collection_behavior")
    // public ResponseResult collectBehavior(@RequestBody CollectionBehaviorDto dto){
    //     return userBehaviorService.collectArticle(dto);
    // }
}
