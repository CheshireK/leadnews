package com.myapp.model.article.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleInfoDto implements Serializable {
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 作者id
     */
    private Integer authorId;
}
