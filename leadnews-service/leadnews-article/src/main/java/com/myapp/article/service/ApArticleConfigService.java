package com.myapp.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myapp.model.article.dto.ArticleStatusDto;
import com.myapp.model.article.pojo.ApArticleConfig;

public interface ApArticleConfigService extends IService<ApArticleConfig> {
    void updateStatus(ArticleStatusDto articleStatusDto);

    void deleteByUpdate(ArticleStatusDto articleStatusDto);
}
