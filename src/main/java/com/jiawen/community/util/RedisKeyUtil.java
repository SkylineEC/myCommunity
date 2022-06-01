package com.jiawen.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    //代表用户收到的赞
    private static final String PREFIX_USER_LIKE = "like:user";

    //为了统计方便 采用两份数据
    //声明前缀 是被关注的目标
    private static final String PREFIX_FOLLOWEE = "followee";

    //声明前缀 是关注者 我关注了某人 存进去我的目标 同时以那个人为key, 也把我也存进去(作为粉丝)
    //他好统计他的粉丝 我好统计我关注的目标
    private static final String PREFIX_FOLLOWER = "follower";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 关注是一个非常高频的功能 需要使用redis实现
    // 某个用户的赞
    // like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //关注可以排序 所以存储一个zset 送进去当前时间
    //关注的目标可以是： 用户 帖子 题目
    //某个用户 关注的某个实体 对应的值 是实体的id
    //followee:userId:entityType -> zset(entityId,now)
    // 某个用户关注的实体类型 ： 实体id
    // followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体(用户 帖子 题目)拥有的粉丝
    //entityType:entityId能够唯一标识一个实体
    // follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

}
