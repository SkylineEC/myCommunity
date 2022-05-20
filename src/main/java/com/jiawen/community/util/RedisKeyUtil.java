package com.jiawen.community.util;

public class RedisKeyUtil {

    //key是由冒号隔开
    //有些单词固定
    private static final String SPLIT = ":";

    //帖子和评论统称实体
    private static final String PREFIX_ENTITY_LIKE = "like:entity";


    //某个实体的赞

    /**
     *
     * like:entity:entityType:entityId -> set(userId) 集合中装的是userId
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
}
