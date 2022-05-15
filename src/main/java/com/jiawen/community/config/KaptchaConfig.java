package com.jiawen.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {
    //因为在Maven导入项目的时候发现它不能通过Spring Boot自动配置
    //所以我们写一个配置类来配置
    //然后让SpringBoot自动加载配置类
    //@Bean 声明之后将被Spring所装配
    //实例被自动装配到Spring容器中
    @Bean
    public Producer kaptchaProducer(){
        Properties properties = new Properties();

        //设置属性
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
        Config config = new Config(properties);
        DefaultKaptcha kaptcha = new DefaultKaptcha();

        kaptcha.setConfig(config);
        return kaptcha;

    }

}
