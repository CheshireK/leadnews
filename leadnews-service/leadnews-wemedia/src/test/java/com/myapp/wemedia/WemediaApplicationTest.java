package com.myapp.wemedia;

import com.myapp.common.tess4j.Tess4jClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WemediaApplicationTest {
    @Autowired
    private Tess4jClient tess4jClient;

    @Test
    public void setTess4jClient(){
        System.out.println(tess4jClient);
        System.out.println(tess4jClient.getDataPath());
        System.out.println(tess4jClient.getLanguage());
    }

}
