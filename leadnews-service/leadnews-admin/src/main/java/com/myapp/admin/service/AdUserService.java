package com.myapp.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myapp.model.admin.dto.AdUserDto;
import com.myapp.model.admin.pojo.AdUser;
import com.myapp.model.common.dto.ResponseResult;

public interface AdUserService extends IService<AdUser> {

    /**
     * 用户登录
     * @param adUserDto 用户信息
     * @return 成功响应
     */
    ResponseResult login(AdUserDto adUserDto);
}
