package com.myapp.api.wemedia;

import com.myapp.api.wemedia.fallback.IWemediaClientFallback;
import com.myapp.model.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "leadnews-wemedia", fallbackFactory = IWemediaClientFallback.class)
public interface IWemediaClient {
    /**
     * 查询全部频道
     * @return 频道列表
     */
    @GetMapping("/api/v1/channel/list")
    public ResponseResult getChannels();
}
