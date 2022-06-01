package com.jiawen.community.service;

import com.jiawen.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞
    // 重构保证事务性

    /**
     *
     * @param userId 用户id
     * @param entityType 实体的类型
     * @param entityId 实体的id
     * @param entityUserId 被赞的那个人 指实体的拥有者 通过实体查找太麻烦 并且需要访问数据库
     *                     所以传进方法的时候也可以传进去实体的userId
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //先找到实体的key
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                //某个实体的拥有者
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                //查询这个userId是否在这个实体的集合里面 来判断是否点过赞
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                //查询不可以放到事务之内

                //开启事务
                operations.multi();

                //如果已经点过赞
                if (isMember) {
                    //取消点赞操作
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    //点赞操作
                    operations.opsForSet().add(entityLikeKey, userId);
                    //普通的String 增加
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}
