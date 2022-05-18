package com.jiawen.community.config;


import com.jiawen.community.controller.intercepter.AlphaIntercepter;
import com.jiawen.community.controller.intercepter.LoginTicketIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //一般都是声明一个第三方的bean 但是拦截器需要实现几个接口
    //注入拦截器后配置
    @Autowired
    private AlphaIntercepter alphaIntercepter;

    @Autowired
    private LoginTicketIntercepter loginTicketIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //不需要拦截静态资源 所以需要排除掉
        // /** 代表static目录下所有的文件夹
        registry.addInterceptor(alphaIntercepter)
                .excludePathPatterns("/**/*.css","/**/*.jpg","/**/*.jpeg","/**/*.js")
                .addPathPatterns("/register","/login");//只拦截这两个路径


        registry.addInterceptor(loginTicketIntercepter)
                .excludePathPatterns("/**/*.css","/**/*.jpg","/**/*.jpeg","/**/*.js");
                //全部路径都要拦截
        //下一步是要在模板中进行处理 去前端改写index.header
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
