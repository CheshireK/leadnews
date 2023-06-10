package com.myapp.search.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApUserSearchServiceTest {

    @Autowired
    ApUserSearchService apUserSearchService;

    @Test
    void save() {
        for (int i =0; i<11; i++) {
            apUserSearchService.save("测试"+i, 1);
        }
    }
}