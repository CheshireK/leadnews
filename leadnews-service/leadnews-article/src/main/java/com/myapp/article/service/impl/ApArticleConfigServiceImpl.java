package com.myapp.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myapp.article.mapper.ApArticleConfigMapper;
import com.myapp.article.service.ApArticleConfigService;
import com.myapp.model.article.dto.ArticleStatusDto;
import com.myapp.model.article.pojo.ApArticleConfig;
import com.myapp.model.wemedia.constatnt.WemediaConstants;
import org.springframework.stereotype.Service;

@Service
public class ApArticleConfigServiceImpl extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig> implements ApArticleConfigService {
    @Override
    public void updateStatus(ArticleStatusDto articleStatusDto) {
        if (articleStatusDto.getId()!=null && articleStatusDto.getEnable()!=null){
            // 当Enable=1时表示上架，down为false表示不下架
            boolean down = ! articleStatusDto.getEnable().equals(WemediaConstants.WM_NEWS_ENABLE);
            LambdaUpdateWrapper<ApArticleConfig> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ApArticleConfig::getIsDown, down);
            updateWrapper.eq(ApArticleConfig::getArticleId, articleStatusDto.getId());
            this.update(updateWrapper);
        }


    }

    @Override
    public void deleteByUpdate(ArticleStatusDto articleStatusDto) {
        if (articleStatusDto==null)return;
        Long articleId = articleStatusDto.getId();
        LambdaUpdateWrapper<ApArticleConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ApArticleConfig::getIsDelete, true);
        updateWrapper.eq(ApArticleConfig::getArticleId, articleId);
        this.update(updateWrapper);
    }
}
