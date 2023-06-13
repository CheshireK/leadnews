package com.myapp.admin.controller;

import com.myapp.model.common.dto.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/channel")
public class ChannelController {

    /**
     * 频道新增
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(){
        return null;
    }

    /**
     * 分页查询频道接口
     */
    @PostMapping("/list")
    public ResponseResult list(){
        return null;
    }
}
