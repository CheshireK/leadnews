package com.myapp.article.interceptor;

import com.myapp.model.user.pojo.ApUser;
import com.myapp.util.thread.ApThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.myapp.model.common.constant.CustomHeader.HEADER_USER_ID;

/**
 * 将网关放置在header中的用户信息，存放在ThreadLocal变量中
 */
@Slf4j
@Component
public class ApTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional.ofNullable(request.getHeader(HEADER_USER_ID))
                .ifPresent(id->{
                    ApUser user = new ApUser();
                    user.setId(Integer.valueOf(id));
                    ApThreadLocalUtil.setUser(user);
                    log.debug("ApTokenInterceptor-设置用户信息到threadlocal中...");
                });
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("ApTokenInterceptor-清理ThreadLocal");
        ApThreadLocalUtil.clear();

    }
}