package com.myapp.wemedia.feign;

import com.myapp.api.wemedia.IWemediaClient;
import com.myapp.model.common.dto.ResponseResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WemediaClientTest {

    @Autowired
    private IWemediaClient wemediaClient;

    @Test
    void getChannels() {
        ResponseResult channels = wemediaClient.getChannels();
        Object data = channels.getData();
        System.out.println(data);
    }
}