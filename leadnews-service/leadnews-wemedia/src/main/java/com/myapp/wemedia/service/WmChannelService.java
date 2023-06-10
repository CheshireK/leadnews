package com.myapp.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.pojo.WmChannel;

public interface WmChannelService extends IService<WmChannel> {
    /**
     * 查询全部频道
     * @return 频道列表
     */
    ResponseResult listAll();
}
