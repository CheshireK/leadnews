package com.myapp.api.wemedia.fallback;

import com.myapp.api.wemedia.IWemediaClient;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IWemediaClientFallback implements FallbackFactory<IWemediaClient> {
    @Override
    public IWemediaClient create(Throwable throwable) {
        return new IWemediaClient() {

            /**
             * 查询全部频道
             * @return 频道列表
             */
            @Override
            public ResponseResult getChannels() {
                log.error("IWemediaClient-getChannels fallback error message={}", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,throwable.getMessage());
            }
        };
    }
}
