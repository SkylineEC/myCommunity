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

    /**
     * 实体类型： 帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型： 帖子
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型: 用户
     */
    int ENTITY_TYPE_USER = 3;


    /**
     *  主题: 评论
     */
    String TOPIC_COMMENT = "comment";

    String TOPIC_LIKE = "like";

    String TOPIC_FOLLOW = "follow";

    int SYSTEM_USER_ID = 1;


    /**
     * 主题: 发帖
     */
    String TOPIC_PUBLISH = "publish";


}
