package com.jiawen.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import com.jiawen.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
/**
 * @program: myCommunity
 * @description:
 * @author: Mr.Wang
 * @create: 2022-06-01 21:58
 **/
@Service
public class FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //被关注目标的key
                //id为userId的用户关注entityType类别的数据
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                //某实体的followers
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();


                //添加被关注实体的id(当前用户的关注列表里面添加项目) 分数是当前时间
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());

                //给这个被关注的实体添加一个follower(当前用户) 分数是当前时间
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    // 查询关注的实体的数量

    /**
     *
     * @param userId 用户id
     * @param entityType 实体的类型 比如当前用户关注的所有用户 当前用户关注的所有post
     * @return
     */
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查询实体的粉丝的数量

    /**
     *
     * @param entityType 这两个参数唯一标识一个实体
     * @param entityId
     * @return
     */
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否已关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        //如果分数非空 那就说明已经关注
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
        //去UserController
    }

}

