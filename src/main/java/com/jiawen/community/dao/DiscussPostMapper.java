package com.jiawen.community.dao;


import com.jiawen.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //要实现分页功能
    //首页查的时候不需要·userID
    //将来要开发用户主页功能 需要查找自己发过的帖子
    //当userID是0的时候 就不管它 不把它拼到SQL里面 这就需要实现动态SQL

    //在开发的时候 要考虑到分页
    //后面跟着两个参数 offset是起始行号 limit是查询的行数（最大）
    //这样就可以实现分页功能

    //每一页多少条数据可以固定下来，总行数是查表查出来的 相除就是页数
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Param是一个别名 如果是在SQL里需要用到动态的条件，

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);



}
