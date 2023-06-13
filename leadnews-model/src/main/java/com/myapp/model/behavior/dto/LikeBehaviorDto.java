package com.myapp.model.behavior.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LikeBehaviorDto implements Serializable {
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 0 点赞   1 取消点赞
     */
    private Short operation;
    /**
     * 0文章  1动态   2评论
     */
    private Short type;
}
