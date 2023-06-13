package com.myapp.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.myapp.api.wemedia.IWemediaClient;
import com.myapp.article.mapper.ApArticleMapper;
import com.myapp.article.service.HotArticleService;
import com.myapp.common.redis.CacheService;
import com.myapp.model.article.bo.HotArticleBo;
import com.myapp.model.article.pojo.ApArticle;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.article.ArticleConstant;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.pojo.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private IWemediaClient wemediaClient;

    @Autowired
    private CacheService cacheService;

    /**
     * 计算热点文章
     */
    @Override
    public void processHotArticle() {
        Date date = DateTime.now().minusYears(3).toDate();
        List<ApArticle> articleList = apArticleMapper.loadArticleListByDate(date, 1000);
        computeScoreOfArticle(articleList);
    }

    private void computeScoreOfArticle(List<ApArticle> articleList) {
        if (articleList==null || articleList.size()==0 ){
            log.error("HotArticleService-computeScoreOfArticle：文章列表为空");
            return;
        }
        // 计算每个文章的分值
        log.debug("HotArticleService-computeScoreOfArticle：计算每个文章的分值");
        List<HotArticleBo> HotArticleBoList
                = articleList.stream().map(HotArticleBo::new)
                .sorted(Comparator.comparing(HotArticleBo::getScore).reversed())
                .collect(Collectors.toList());


        // 获取全部频段
        log.debug("HotArticleService-computeScoreOfArticle：获取全部频段");
        ResponseResult responseResult = wemediaClient.getChannels();
        if (!responseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            log.error("HotArticleService-computeScoreOfArticle：远程接口调用失败: IWemediaClient-getChannels: 获取全部频道, error message={}",
                    responseResult.getErrorMessage());
            return;
        }
        String channelArray = JSON.toJSONString(responseResult.getData());
        List<WmChannel> wmChannels = JSON.parseArray((channelArray), WmChannel.class);
        if (wmChannels == null || wmChannels.size() == 0) {
            log.error("HotArticleService-computeScoreOfArticle：频道不能为空");
            return;
        }
        // 过滤出每个频道的文章，并排序
        // 将这个频道的文章计算分值，并将前30篇文章缓存到redis中
        for (WmChannel wmChannel : wmChannels) {
            List<HotArticleBo> collect = HotArticleBoList.stream()
                    .filter(article -> article.getChannelId().equals(wmChannel.getId()))
                    // 按分值降序排列
                    .sorted(Comparator.comparing(HotArticleBo::getScore).reversed())
                    // 限制每个频道，缓存的热点文章为30篇
                    .limit(30)
                    // 缓存到redis中
                    .collect(Collectors.toList());
            String key = ArticleConstant.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId();
            cacheToRedis(key, collect);
        }
        // 缓存推荐页的文章
        String suggestionPage = ArticleConstant.HOT_ARTICLE_FIRST_PAGE + ArticleConstant.DEFAULT_TAG;
        List<HotArticleBo> collect = HotArticleBoList.stream().limit(30).collect(Collectors.toList());
        cacheToRedis(suggestionPage, collect);

    }

    private void cacheToRedis(String key, List<HotArticleBo> collect) {
        cacheService.set(key, JSON.toJSONString(collect));
        log.debug("HotArticleService-computeScoreOfArticle：缓存热点文章到对应频道, redis key={}", key);
    }
}
