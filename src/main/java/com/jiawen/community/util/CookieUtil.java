package com.jiawen.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {


    //静态方法不需要容器管理
    public static String getValue(HttpServletRequest request,
                                  String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("参数为空");
        }

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                //如果cookie的name是我想找的参数
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        //没有找到我想要的数据
        return  null;
    }
}
