package com.platform.serve.config.filter;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 跨域拦截请求
 *
 * @Author lds
 * @Date 2021/4/27 10:49
 */

public class CrossFilter implements HandlerInterceptor {


//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws IOException, ServletException {
//
//        log.info("----------CrossFilter：【允许跨域访问】refererUrl【{}】----------", request.getHeader("Referer"));
//        //允许跨域访问
//        response.setContentType("text/html;charset=UTF-8");
//        //response.setContentType("application/x-download");
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//        response.setHeader("Access-Control-Max-Age", "36000");
//        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,Authorization,SessionToken,JSESSIONID,token");
//        response.setHeader("XDomainRequestAllowed", "1");
//        //自定义
//        response.setHeader("Access-Control-Expose-Headers", "userCode, Content-Disposition");
//
//        //漏洞：关闭IE的文档类型自动判断功能
//        response.setHeader("X-Content-Type-Options", "nosniff");
//
//        ArrayList<String> strings = Lists.newArrayList("POST", "GTE");
//
//        if (strings.contains(request.getMethod())) {
//            log.info("----------CrossFilter：【预请求】默认通过----------");
//            response.setStatus(HttpServletResponse.SC_OK);
//        } else {
//            log.info("----------CrossFilter：【正式请求】执行下一个过滤器----------");
//            filterChain.doFilter(request, response);
//        }
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        if (request.getHeader(HttpHeaders.ORIGIN) != null) {
            String origin = request.getHeader("Origin");
            response.addHeader("Access-Control-Allow-Origin", origin);
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT,PATCH, HEAD");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            response.addHeader("Access-Control-Max-Age", "3600");
        }
        return true;
    }

}
