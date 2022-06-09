package com.jiawen.community.controller.interceptor;


import com.jiawen.community.entity.LoginTicket;
import com.jiawen.community.entity.User;
import com.jiawen.community.service.UserService;
import com.jiawen.community.util.CookieUtil;
import com.jiawen.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    //在请求一开始就获取ticket 来查找有没有对应的user
    //为什么一开始就做? 因为在请求的时候 随时都可能调取用户数据

    @Autowired
    private UserService userService;

    @Autowired
    private  HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //通过cookie获取ticket
        //cookie是request传过来的  要从cookie中获取凭证
        String ticket = CookieUtil.getValue(request,"ticket");
        //如果ticket不是空的 代表已经登录了
        if(ticket != null){
            //登录的时候 需要查询ticket 获取用户的数据
            LoginTicket loginTicket = userService.findLoginTicket(ticket);

            //需要缺人是否有效 并且确认是否超时

            //不为空 状态0 有效 没有超时 如果都满足
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //凭证有效 登录成功状态 这样就可以查询User

                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中 持有用户 就要存储User
                //浏览器访问服务器 是多对一并且并发的 每一个浏览器访问服务器 都要创立一个线程
                //要考虑多线程的情况
                //多线程并发访问 隔离没有问题
                //多线程 工具 : ThreadLocal 写了一个工具类
                //将数据存到当前线程的map里面 在整个处理请求的过程中 线程都是活着的
                hostHolder.setUser(user);


            }
        }

        return true;
    }


    //什么时候需要用user呢？
    //在模板引擎被调用之前就要用
    //所以要存到model里面
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
            //这时候model里面已经有了user 就可以拿来用

        }


    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //执行完之后把user数据清理掉
        hostHolder.clear();
    }
}
