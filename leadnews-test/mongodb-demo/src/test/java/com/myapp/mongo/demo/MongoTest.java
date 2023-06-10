package com.myapp.mongo.demo;

import com.mongodb.client.result.DeleteResult;
import com.myapp.mongo.demo.pojo.ApAssociateWords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MongoTest {
    @Autowired
    private MongoTemplate template;

    @Test
    public void test(){
        for (int i = 0; i< 100; i++) {
            ApAssociateWords words = new ApAssociateWords();
            words.setAssociateWords("测试");
            words.setCreatedTime(new Date());
            template.save(words, "ap_associate_words");
        }
    }

    /**
     * 查询一个文档
     */
    @Test
    public void findOne(){
        ApAssociateWords words = template.findById("64819aaab756106d9ac166a8", ApAssociateWords.class);
        System.out.println(words);
    }


    /**
     * 条件查询
     */
    @Test
    public void testQuery(){
        Query query = Query.query(Criteria.where("associateWords").is("测试"))
                        .with(Sort.by(Sort.Direction.DESC, "createdTime"));
        List<ApAssociateWords> wordsList = template.find(query, ApAssociateWords.class);
        System.out.println(wordsList);
        System.out.println(wordsList.size());

    }

    @Test
    public void testDel(){
        DeleteResult remove = template.remove(Query.query(Criteria.where("associateWords")
                .is("测试")), ApAssociateWords.class);
        System.out.println(remove);
    }
}
