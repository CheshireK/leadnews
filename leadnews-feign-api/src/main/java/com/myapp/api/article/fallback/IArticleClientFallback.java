package com.myapp.api.article.fallback;

import com.myapp.api.article.IArticleClient;
import com.myapp.model.article.dto.ArticleDto;
import com.myapp.model.behavior.dto.CollectionBehaviorDto;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IArticleClientFallback implements FallbackFactory<IArticleClient> {


    /**
     * Returns an instance of the fallback appropriate for the given cause.
     *
     * @param cause cause of an exception.
     * @return fallback
     */
    @Override
    public IArticleClient create(Throwable cause) {
        return new IArticleClient() {
            @Override
            public ResponseResult saveArticle(ArticleDto dto) {
                log.error("IArticleClient-saveArticle-fallback error={}",cause.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "服务出现故障,获取数据失败");
            }

            @Override
            public ResponseResult collectArticle(CollectionBehaviorDto dto) {
                log.error("IArticleClient-collectArticle-fallback error={}",cause.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "服务出现故障,获取数据失败");
            }
        };
    }
}
