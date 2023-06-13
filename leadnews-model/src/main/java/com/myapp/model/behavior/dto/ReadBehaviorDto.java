package com.myapp.model.behavior.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReadBehaviorDto implements Serializable {
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 阅读次数
     */
    private Integer count;
}
