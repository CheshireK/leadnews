package com.myapp.wemedia.controller.v1;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/channel")
public class WmChannelController {

    @Autowired
    private WmChannelService channelService;

    /**
     * 查询所有频道
     *
     * @return
     */
    @GetMapping("/channels")
    public ResponseResult channels() {
        return channelService.listAll();
    }
}
