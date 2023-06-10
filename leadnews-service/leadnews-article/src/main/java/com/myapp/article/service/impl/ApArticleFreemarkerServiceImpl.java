package com.myapp.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.myapp.article.mapper.ApArticleConfigMapper;
import com.myapp.article.mapper.ApArticleMapper;
import com.myapp.article.service.ApArticleFreemarkerService;
import com.myapp.file.service.FileStorageService;
import com.myapp.model.article.pojo.ApArticle;
import com.myapp.model.common.constant.ArticleMessageConstant;
import com.myapp.model.search.vo.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ApArticleFreemarkerServiceImpl implements ApArticleFreemarkerService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleConfigMapper configMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private ApArticleMapper articleMapper;

    /**
     * 生成静态文件上传到minIO中
     * 发送异步消息，通知es添加文章索引
     * @param article
     * @param content
     */
    @Override
    @Async
    public void buildApArticleToMinio(ApArticle article, String content) {
        if (StringUtils.hasLength(content)) {
            Template template = null;
            StringWriter out = new StringWriter();
            try {
                template = configuration.getTemplate("article.ftl");
                //数据模型
                Map<String,Object> contentDataModel = new HashMap<>();
                contentDataModel.put("content", JSONArray.parseArray(content));
                //合成
                template.process(contentDataModel,out);
            } catch (Exception e) {
                e.printStackTrace();
            }
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            String filename = article.getId() + ".html";
            String url = fileStorageService.uploadHtmlFile("", filename, in);
            article.setStaticUrl(url);
            log.info("生成静态文件,上传文件到minio, filename={}, url={}", filename, url);
            articleMapper.updateById(article);
            // 发送异步消息，通知es添加文章索引
            saveArticleVoToEs(article, content, url);

        }
    }

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    /**
     * 发送异步消息，通知es添加文章索引
     * @param article app 文章
     * @param content 文章内容
     * @param staticUrl minio静态url
     */
    private void saveArticleVoToEs(ApArticle article, String content, String staticUrl){
        SearchArticleVo articleVo = new SearchArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setContent(content);
        articleVo.setStaticUrl(staticUrl);
        kafkaTemplate.send(ArticleMessageConstant.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(articleVo));
        log.info("向kafka发送消息，通知es添加文章索引 topic={}, articleId={}, staticUrl={}",
                ArticleMessageConstant.ARTICLE_ES_SYNC_TOPIC, article.getId(), staticUrl);
    }
}
