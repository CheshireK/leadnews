package com.myapp.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myapp.article.mapper.ApArticleConfigMapper;
import com.myapp.article.mapper.ApArticleContentMapper;
import com.myapp.article.mapper.ApArticleMapper;
import com.myapp.article.service.ApArticleService;
import com.myapp.common.exception.CustomException;
import com.myapp.file.service.FileStorageService;
import com.myapp.model.article.dto.ArticleDto;
import com.myapp.model.article.dto.ArticleHomeDto;
import com.myapp.model.article.pojo.ApArticle;
import com.myapp.model.article.pojo.ApArticleConfig;
import com.myapp.model.article.pojo.ApArticleContent;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private ApArticleContentMapper articleContentMapper;

    @Autowired
    private ApArticleConfigMapper articleConfigMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private Configuration configuration;

    /**
     * 根据参数加载文章列表
     *
     * @param loadType       1为加载更多  2为加载最新
     * @param articleHomeDto
     * @return
     */
    @Override
    public ResponseResult load(short loadType, ArticleHomeDto articleHomeDto) {
        Integer size = articleHomeDto.getSize();
        if (size == null || size <= 0) {
            size = 10;
        }
        size = Math.min(size, MAX_PAGE_SIZE);

        if (Objects.equals(LOADTYPE_LOAD_MORE, loadType) || Objects.equals(LOADTYPE_LOAD_NEW, loadType)) {
            loadType = LOADTYPE_LOAD_MORE;
        }

        if (!StringUtils.hasLength(articleHomeDto.getTag())) {
            articleHomeDto.setTag(DEFAULT_TAG);
        }

        if (articleHomeDto.getMinBehotTime() == null) {
            articleHomeDto.setMinBehotTime(new Date());
        }

        if (articleHomeDto.getMaxBehotTime() == null) {
            articleHomeDto.setMaxBehotTime(new Date());
        }

        List<ApArticle> articles = articleMapper.loadArticleList(loadType, articleHomeDto);

        return ResponseResult.okResult(articles);
    }

    /**
     * 保存文章
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public ResponseResult saveOrUpdateArticle(ArticleDto dto) {
        // 1.检查参数
        if (dto == null || dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle article = new ApArticle();
        BeanUtils.copyProperties(dto, article);
        // 2.判断是否存在id
        // 2.1 不存在id  保存  文章  文章配置  文章内容
        Long articleId = dto.getId();
        try {
            if (articleId == null) {
                // 保存文章
                baseMapper.insert(article);

                // 保存配置
                ApArticleConfig articleConfig = new ApArticleConfig(articleId);
                articleConfigMapper.insert(articleConfig);

                // 保存 文章内容
                ApArticleContent content = new ApArticleContent();
                content.setArticleId(articleId);
                content.setContent(dto.getContent());
                articleContentMapper.insert(content);
            }
            // 2.2 存在id   修改  文章  文章内容
            else {
                // 修改  文章
                int update = baseMapper.updateById(article);

                // 修改文章内容
                ApArticleContent content = articleContentMapper.selectOne(new LambdaQueryWrapper<ApArticleContent>()
                        .eq(ApArticleContent::getArticleId, article.getId()));
                if (update == 0 || content == null) {
                    return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,
                            "文章没有找到");
                }
                content.setContent(dto.getContent());
                articleContentMapper.updateById(content);
            }
        } catch (Exception e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        // 3.结果返回  文章的id
        return ResponseResult.okResult(article.getId());
    }
}
