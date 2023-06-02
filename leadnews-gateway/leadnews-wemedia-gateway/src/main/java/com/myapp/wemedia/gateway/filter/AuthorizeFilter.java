package com.myapp.wemedia.gateway.filter;

import com.myapp.util.common.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.myapp.model.common.constant.AppConstant.TOKEN_ERROR;
import static com.myapp.model.common.constant.AppConstant.TOKEN_OUT_OF_DATE;
import static com.myapp.model.common.constant.CustomHeader.HEADER_TOKEN;
import static com.myapp.model.common.constant.CustomHeader.HEADER_USER_ID;

/**
 * 验证请求头中的token参数，除了`/login`请求之外，都需要携带`token`参数，且合法
 */
@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();

        // 放行登录请求
        if (request.getURI().getPath().contains("/login")) {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst(HEADER_TOKEN);

        if (!StringUtils.hasLength(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (claimsBody == null || result == TOKEN_ERROR || result == TOKEN_OUT_OF_DATE) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
