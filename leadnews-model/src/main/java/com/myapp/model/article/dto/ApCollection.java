package com.myapp.model.article.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * APP收藏信息表
 * </p>
 *
 * @author luosa
 * @since 2023-06-12
 */
@TableName("ap_collection")
@ApiModel(value = "ApCollection对象", description = "APP收藏信息表")
public class ApCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("实体ID")
    private Integer entryId;

    @ApiModelProperty("文章ID")
    private Long articleId;

    @ApiModelProperty("点赞内容类型	            0文章	            1动态")
    private Short type;

    @ApiModelProperty("创建时间")
    private Date collectionTime;

    @ApiModelProperty("发布时间")
    private Date publishedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Date getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Date collectionTime) {
        this.collectionTime = collectionTime;
    }

    public Date getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(Date publishedTime) {
        this.publishedTime = publishedTime;
    }

    @Override
    public String toString() {
        return "ApCollection{" +
            "id = " + id +
            ", entryId = " + entryId +
            ", articleId = " + articleId +
            ", type = " + type +
            ", collectionTime = " + collectionTime +
            ", publishedTime = " + publishedTime +
        "}";
    }
}
