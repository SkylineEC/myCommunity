package com.jiawen.community.dao;


import com.jiawen.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //查询评论是要支持分页的 需要提供两个方法
    //1. 每页有多少条数据
    //2. 一共有多少条数据
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType,int entityId);

    int insertComment(Comment comment);
}
