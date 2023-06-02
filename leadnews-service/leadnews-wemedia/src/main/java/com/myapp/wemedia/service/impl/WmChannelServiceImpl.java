package com.myapp.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.pojo.WmChannel;
import com.myapp.wemedia.mapper.WmChannelMapper;
import com.myapp.wemedia.service.WmChannelService;
import org.springframework.stereotype.Service;

@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService{
    @Override
    public ResponseResult listAll() {
        return ResponseResult.okResult(list());
    }
}
