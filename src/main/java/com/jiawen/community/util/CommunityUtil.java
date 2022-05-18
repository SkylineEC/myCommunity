package com.jiawen.community.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.security.DigestException;
import java.util.HashMap;
import java.util.Map;

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

    /**
     *
     * @param code 编号
     * @param msg 消息
     * @param map 业务数据
     * @return
     */
    public static String getJsonString(int code, String msg, Map<String, Object> map){
        //参数封装到json对象里面
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJsonString(int code, String msg){
        return getJsonString(code,msg,null);
    }

    public static String getJsonString(int code){
        return getJsonString(code,null,null);
    }
    public static void main(String[] args){
        Map<String,Object> map = new HashMap<>();
        map.put("name","Zhangsan");
        map.put("age",25);
        System.out.println(getJsonString(0,"ok",map));
    }

}
