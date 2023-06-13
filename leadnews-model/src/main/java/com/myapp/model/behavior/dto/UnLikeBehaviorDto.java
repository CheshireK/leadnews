package com.myapp.model.behavior.dto;

import lombok.Data;

@Data
public class UnLikeBehaviorDto {
    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 0 确认不喜欢      1 取消不喜欢
     */
    private Short type;
}
