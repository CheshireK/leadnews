package com.myapp.model.behavior.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CollectionBehaviorDto implements Serializable {
    /**
     * 文章id
     */
    private Long entryId;

    /**
     * 0收藏    1取消收藏
     */
    private Short operation;

    /**
     * 发布时间
     */
    private Date publishedTime;

    /**
     * 0文章    1动态
     */
    private Short type;
}
