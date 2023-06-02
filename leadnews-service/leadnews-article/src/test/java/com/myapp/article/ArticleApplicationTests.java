package com.myapp.article;

import com.alibaba.fastjson.JSONArray;
import com.myapp.article.mapper.ApArticleContentMapper;
import com.myapp.article.mapper.ApArticleMapper;
import com.myapp.file.service.FileStorageService;
import com.myapp.model.article.pojo.ApArticle;
import com.myapp.model.article.pojo.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
public class ArticleApplicationTests {
    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    Configuration configuration;

    @Autowired
    ApArticleMapper articleMapper;

    @Autowired
    ApArticleContentMapper articleContentMapper;

    @Test
    public void test(){
        ApArticleContent apArticleContent = articleContentMapper.selectById(1302862388957036545L);
        if (apArticleContent!=null && StringUtils.hasLength(apArticleContent.getContent())){
            try {
                Template template = configuration.getTemplate("article.ftl");
                StringWriter writer = new StringWriter();
                Map<String,Object> map = new HashMap<>();
                map.put("content", JSONArray.parseArray(apArticleContent.getContent()));
                template.process(map, writer);

                InputStream is = new ByteArrayInputStream(writer.toString().getBytes());
                String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", is);
                ApArticle apArticle = new ApArticle();
                apArticle.setId(apArticleContent.getArticleId());
                apArticle.setStaticUrl(path);
                System.out.println("apArticle = " + apArticle);
                articleMapper.updateById(apArticle);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }


}
