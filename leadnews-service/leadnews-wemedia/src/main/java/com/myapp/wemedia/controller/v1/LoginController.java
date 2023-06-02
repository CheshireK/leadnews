package com.myapp.wemedia.controller.v1;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.dto.WmLoginDto;
import com.myapp.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/in")
    public ResponseResult in(@RequestBody WmLoginDto dto){
        return wmUserService.login(dto);
    }
}
