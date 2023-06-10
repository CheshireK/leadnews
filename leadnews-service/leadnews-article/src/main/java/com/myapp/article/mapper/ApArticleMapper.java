package com.myapp.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myapp.model.article.dto.ArticleHomeDto;
import com.myapp.model.article.pojo.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    List<ApArticle> loadArticleList(short loadType, ArticleHomeDto dto);

    List<ApArticle> loadArticleListByDate(@Param("date") Date date, Integer limit);
}
