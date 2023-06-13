package com.myapp.admin.interceptor;

import com.myapp.model.admin.pojo.AdUser;
import com.myapp.util.thread.AdThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.myapp.model.common.constant.CustomHeader.HEADER_USER_ID;

@Component
@Slf4j
public class AdTokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional.ofNullable(request.getHeader(HEADER_USER_ID))
                .ifPresent(id->{
                    AdUser user = new AdUser();
                    user.setId(Integer.valueOf(id));
                    AdThreadLocalUtil.setUser(user);
                    log.debug("AdTokenInterceptor设置用户信息到ThreadLocal中...");
                });
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("清理ThreadLocal");
        AdThreadLocalUtil.clear();
    }
}
