package com.myapp.user.controller.v1;

import com.myapp.model.common.dto.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class ApUserFollowController {

    @PostMapping("/user_follow")
    public ResponseResult userFollow(){
        return null;
    }
}
