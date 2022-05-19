package com.jiawen.community.service;


import com.jiawen.community.dao.DiscussPostMapper;
import com.jiawen.community.entity.DiscussPost;
import com.jiawen.community.util.SensitiveFilter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;


@Service
public class DiscussPostService  {

    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Autowired
    SensitiveFilter sensitiveFilter;
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if (post == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //对post数据做敏感词过滤
        //只对content和title进行过滤
        //对标签也去掉 <script/>
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setTitle(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);

    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

}
