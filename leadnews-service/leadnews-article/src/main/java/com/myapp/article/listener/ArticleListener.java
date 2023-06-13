package com.myapp.article.listener;

import com.alibaba.fastjson.JSON;
import com.myapp.article.service.ApArticleConfigService;
import com.myapp.model.article.dto.ArticleStatusDto;
import com.myapp.model.wemedia.constatnt.WmNewsMessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class ArticleListener {
    @Autowired
    private ApArticleConfigService articleConfigService;

    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void upOrdDown(String message){
        if (StringUtils.hasLength(message)){
            ArticleStatusDto articleStatusDto = JSON.parseObject(message, ArticleStatusDto.class);
            articleConfigService.updateStatus(articleStatusDto);
            log.info("接收到kafka消息：修改文章状态，article id={} enable={}",
                    articleStatusDto.getId(), articleStatusDto.getEnable());
        }

    }

    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_DELETE_TOPIC)
    public void delete(String message){
        ArticleStatusDto articleStatusDto = JSON.parseObject(message, ArticleStatusDto.class);
        articleConfigService.deleteByUpdate(articleStatusDto);
        log.info("接收到kafka消息：删除文章，article id={} ",
                articleStatusDto.getId());
    }
}
