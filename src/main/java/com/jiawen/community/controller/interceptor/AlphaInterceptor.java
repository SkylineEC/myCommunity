package com.jiawen.community.controller.interceptor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class AlphaInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);
    /*
    在controller之前执行 返回的是布尔值 是否执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("pre handle: " + handler.toString());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //请求已经处理完 需要模板引擎了
        logger.debug("post handle: " + handler.toString());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }


    /*
    是在TemplateEngine之后执行
    如果在调用controller过程中有异常 可以对异常进行处理
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("after handle: " + handler.toString());
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
