package com.jiawen.community.service;


import com.jiawen.community.dao.CommentMapper;
import com.jiawen.community.entity.Comment;
import com.jiawen.community.util.CommunityConstant;
import com.jiawen.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;
    /**
     *
     * @param entityType
     * @param entityId
     * @param offset 第几页开始
     * @param limit 限制
     * @return 一列的Comment
     */
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    /**
     *
     * @param entityType
     * @param entityId
     * @return 返回帖子的数量
     */
    public int findCommentCount(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    /**
     *
     * @param comment
     * @return
     * 里面包含两次dao操作 所以进行事务管理操作 当前整个方法就在一个事务之内
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        //有必要对comment进行过滤
        //注入
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        int rows = commentMapper.insertComment(comment);


        //更新评论数量
        //只有更新的是帖子的评论数量 才会
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            //查到帖子数量
            int count = commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());

            discussPostService.updateCommentCount(comment.getEntityId(),count);

        }

        return rows;
    }


}
