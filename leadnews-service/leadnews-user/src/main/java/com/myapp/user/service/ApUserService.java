package com.myapp.user.service;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.user.dto.LoginDto;

public interface ApUserService {

    ResponseResult login(LoginDto loginDto);
}
