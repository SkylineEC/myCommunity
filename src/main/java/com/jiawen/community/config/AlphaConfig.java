package com.jiawen.community.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

//演示 装配一个配置类
@Configuration
public class AlphaConfig {


    //这个方法返回的对象将会装配到容器里 方法名就默认是bean的名字
    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd");
    }
}
