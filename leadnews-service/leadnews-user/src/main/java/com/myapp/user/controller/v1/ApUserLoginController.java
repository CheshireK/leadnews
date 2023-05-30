package com.myapp.user.controller.v1;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.user.dto.LoginDto;
import com.myapp.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
@Api(value = "app端用户登录", tags = "ap_user")
public class ApUserLoginController {

    @Autowired
    private ApUserService apUserService;


    @PostMapping("/login_auth")
    @ApiOperation(value = "用户登录")
    public ResponseResult login(@RequestBody LoginDto loginDto){
        ResponseResult result = apUserService.login(loginDto);
        return result;
    }
}
