package com.myapp.app.gateway.filter;

import com.myapp.util.common.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.myapp.model.common.constant.CustomHeader.HEADER_TOKEN;
import static com.myapp.model.common.constant.CustomHeader.HEADER_USER_ID;

/**
 * 获取Header中的token解析用户信息，将用户放入头信息中
 */
@Slf4j
@Component
public class HeaderProcessFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst(HEADER_TOKEN);
        // 获取token中的用户信息
        Claims claimsBody = null;
        try {
            claimsBody = AppJwtUtil.getClaimsBody(token);
            if (claimsBody!=null){
                Object userId = claimsBody.get("id");
                log.debug("token中获取的id为{}", userId);
                ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                    httpHeaders.add(HEADER_USER_ID, userId.toString());
                }).build();
                // 重置header
                exchange.mutate().request(serverHttpRequest).build();
            }
        } catch (Exception e) {
            log.debug("token解析失败");
        }


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
