package com.myapp.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myapp.article.mapper.ApArticleMapper;
import com.myapp.article.service.ApArticleService;
import com.myapp.model.article.dto.ArticleHomeDto;
import com.myapp.model.article.pojo.ApArticle;
import com.myapp.model.common.dto.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.myapp.model.common.constant.ArticleConstant.*;

@Service
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    private final static short MAX_PAGE_SIZE = 50;

    @Autowired
    private ApArticleMapper articleMapper;

    @Override
    public ResponseResult load(short loadType, ArticleHomeDto articleHomeDto) {
        Integer size = articleHomeDto.getSize();
        if (size == null || size <= 0){
            size = 10;
        }
        size = Math.min(size,MAX_PAGE_SIZE);

        if (Objects.equals(LOADTYPE_LOAD_MORE, loadType) || Objects.equals(LOADTYPE_LOAD_NEW, loadType)){
            loadType = LOADTYPE_LOAD_MORE;
        }

        if (!StringUtils.hasLength(articleHomeDto.getTag())) {
            articleHomeDto.setTag(DEFAULT_TAG);
        }

        if (articleHomeDto.getMinBehotTime()==null) {
            articleHomeDto.setMinBehotTime(new Date());
        }

        if (articleHomeDto.getMaxBehotTime()==null) {
            articleHomeDto.setMaxBehotTime(new Date());
        }

        List<ApArticle> articles = articleMapper.loadArticleList(loadType, articleHomeDto);

        return ResponseResult.okResult(articles);
    }
}
