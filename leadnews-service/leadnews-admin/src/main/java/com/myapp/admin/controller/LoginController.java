package com.myapp.admin.controller;

import com.myapp.admin.service.AdUserService;
import com.myapp.model.admin.dto.AdUserDto;
import com.myapp.model.common.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/login")
@RestController
public class LoginController {

    @Autowired
    private AdUserService adUserService;

    /**
     * 用户登录
     * @return
     */
    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdUserDto adUserDto){
        return adUserService.login(adUserDto);
    }
}
