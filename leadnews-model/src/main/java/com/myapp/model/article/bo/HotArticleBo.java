package com.myapp.model.article.bo;

import com.alibaba.fastjson.annotation.JSONField;
import com.myapp.model.article.pojo.ApArticle;
import com.myapp.model.common.constant.ArticleConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HotArticleBo extends ApArticle {
    @JSONField(serialize = false)
    private Integer score;

    public HotArticleBo(ApArticle article){
        BeanUtils.copyProperties(article, this);
        Integer score = 0;
        if (article.getLikes() != null) {
            score += article.getLikes() * ArticleConstant.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (article.getViews() != null) {
            score += article.getViews();
        }
        if (article.getComment() != null) {
            score += article.getComment() * ArticleConstant.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if (article.getCollection() != null) {
            score += article.getCollection() * ArticleConstant.HOT_ARTICLE_COLLECTION_WEIGHT;
        }
        this.score = score;
    }
}
