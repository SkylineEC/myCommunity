package com.jiawen.community.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//使用元注解 指定用来描述方法
@Target(ElementType.METHOD)
//运行的时候有效
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {


}
