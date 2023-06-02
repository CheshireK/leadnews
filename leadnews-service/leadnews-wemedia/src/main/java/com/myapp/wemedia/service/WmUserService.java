package com.myapp.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.dto.WmLoginDto;
import com.myapp.model.wemedia.pojo.WmUser;

public interface WmUserService extends IService<WmUser> {

    /**
     * 自媒体端登录
     * @param dto
     * @return
     */
    public ResponseResult login(WmLoginDto dto);

}