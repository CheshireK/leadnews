package com.myapp.wemedia.feign;

import com.myapp.api.wemedia.IWemediaClient;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WemediaClient implements IWemediaClient {

    @Autowired
    private WmChannelService channelService;

    /**
     * 查询全部频道
     * @return 频道列表
     */
    @Override
    public ResponseResult getChannels() {
        return channelService.listAll();
    }
}
