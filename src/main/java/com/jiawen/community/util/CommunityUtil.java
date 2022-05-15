package com.jiawen.community.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.security.DigestException;

public class CommunityUtil {


    //生成随机字符串 这是一个公有的静态的方法
    public static String generateUUID() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");


    }//通常由字母和横线组成

    //MD5加密
    //只能加密 不能解密

    //不管是什么密码 都要加一个随机的字符串(salt) 这样之后MD5加密的结果 更加安全
    public static String md5(String key) {
        //加密后的字符串
        //"如果参数是空的" 难就不做处理
        if(StringUtils.isBlank(key)){
            return null;
        }
        //String转bytes
        //加密后的字符串
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

}
