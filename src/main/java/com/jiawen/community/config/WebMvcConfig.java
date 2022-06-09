package com.jiawen.community.config;


import com.jiawen.community.controller.interceptor.AlphaInterceptor;
import com.jiawen.community.controller.interceptor.LoginRequiredInterceptor;
import com.jiawen.community.controller.interceptor.LoginTicketInterceptor;
import com.jiawen.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //一般都是声明一个第三方的bean 但是拦截器需要实现几个接口
    //注入拦截器后配置
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //不需要拦截静态资源 所以需要排除掉
        // /** 代表static目录下所有的文件夹
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.jpg","/**/*.jpeg","/**/*.js")
                .addPathPatterns("/register","/login");//只拦截这两个路径


        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.jpg","/**/*.jpeg","/**/*.js");
                //全部路径都要拦截
        //下一步是要在模板中进行处理 去前端改写index.header


        //我希望处理谁 就在谁上面加上注解
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.jpg","/**/*.jpeg","/**/*.js");
        WebMvcConfigurer.super.addInterceptors(registry);

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
