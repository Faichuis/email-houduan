package com.platform.serve.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 判断是否登陆的 filter
 *
 * @Author lds
 * @Date 2021/4/28 17:02
 */
@Slf4j
@WebFilter(filterName = "loginFilter", urlPatterns = {"/config/*", "/api/*", "/login/user/updateUserInfo"})
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("判断是否登陆的 filter:init");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        String userCode = request.getHeader("userCode");
        if (StringUtils.isNotEmpty(userCode)) {
            log.info("判断是否登陆的 filter:{}登录用户{}", path, userCode);
            chain.doFilter(req, res);
        } else {
            log.info("判断是否登陆的 filter:{}匿名用户", path);
            //匿名用户重定向到登录页面
            //response.sendRedirect("location");
            response.sendRedirect("https://www.baidu.com");
        }
    }

    @Override
    public void destroy() {
        log.info("判断是否登陆的 filter:destroy");
    }


}
