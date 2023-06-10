package com.myapp.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.myapp.model.common.constant.ArticleIndexConstant;
import com.myapp.model.search.vo.SearchArticleVo;
import com.myapp.search.service.ApArticleDocService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Service
@Slf4j
public class ApArticleDocServiceImpl implements ApArticleDocService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 向es建立app article索引
     * @param articleVoJSON article json字符串
     */
    @Override
    public void saveArticleDoc(String articleVoJSON) {
        if (StringUtils.isEmpty(articleVoJSON)){
            return;
        }
        try {
            SearchArticleVo searchArticleVo = JSON.parseObject(articleVoJSON, SearchArticleVo.class);
            if (searchArticleVo.getId() == null || StringUtils.isEmpty(searchArticleVo.getContent())){
                return;
            }
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.index(ArticleIndexConstant.INDEX)
                    .id(searchArticleVo.getId().toString())
                    .source(articleVoJSON, XContentType.JSON);
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info("Kafka向es建立app article索引 id={}", searchArticleVo.getId());
        } catch (IOException e) {
            log.error("Kafka消费信息失败,error message={}", e.getMessage());
        }
    }
}
