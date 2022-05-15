package com.jiawen.community.util;

public interface CommunityConstant {
    //激活成功

    int ACTIVATION_SUCCESS = 0;
    //激活重复
    int ACTIVATION_REPEAT = 1;
    //激活失败
    int ACTIVATION_FAILURE = 2;


    /*
    默认登录状态超时时间 半天
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /*
    记住状态下登录凭证超时时间 100天 三个月
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;






}
