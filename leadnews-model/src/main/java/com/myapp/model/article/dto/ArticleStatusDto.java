package com.myapp.model.article.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleStatusDto implements Serializable {
    /**
     * app文章id
     */
    private Long id;
    /**
     * 上下架：上架 1，下架 0
     */
    private Short enable;
    
}