package com.myapp.search.service;

import com.myapp.model.search.pojo.ApAssociateWord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ApAssociateWordServiceTest {

    @Autowired
    private ApAssociateWordService apAssociateWordService;

    @Test
    void saveAssociateWordList() {

        List<ApAssociateWord> wordList= new ArrayList<>();

        apAssociateWordService.saveAssociateWordList(wordList);
    }
}