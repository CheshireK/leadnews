package com.myapp.wemedia.interceptor;


import com.myapp.model.wemedia.pojo.WmUser;
import com.myapp.util.thread.WmThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

import static com.myapp.model.common.constant.CustomHeader.HEADER_USER_ID;

/**
 * 将网关放置在header中的用户信息，存放在ThreadLocal变量中
 */
@Slf4j
@Component
public class WmTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional.ofNullable(request.getHeader(HEADER_USER_ID))
                .ifPresent(id->{
                    WmUser user = new WmUser();
                    user.setId(Integer.valueOf(id));
                    WmThreadLocalUtil.setUser(user);
                    log.debug("wmTokenFilter设置用户信息到threadlocal中...");
                });
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("清理ThreadLocal");
        WmThreadLocalUtil.clear();

    }
}
