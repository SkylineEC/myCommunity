package com.jiawen.community.controller;

import com.jiawen.community.entity.Comment;
import com.jiawen.community.entity.DiscussPost;
import com.jiawen.community.entity.Event;
import com.jiawen.community.event.EventProducer;
import com.jiawen.community.service.CommentService;
import com.jiawen.community.service.DiscussPostService;
import com.jiawen.community.util.CommunityConstant;
import com.jiawen.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    //我希望在评论完重定向到本页面 但是本页面路径是包含帖子id的 所以我们要获取贴子id
    @RequestMapping(path = "/add/{discussPostId}" ,method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussPostId,
                             Comment comment
                             ){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        //添加通知 触发评论事件
        Event event = new Event().setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);
        //因为comment分两种 一种是对帖子的评论 一种是对评论的评论 在下面都叫target
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            //要设置Entity所属的用户id
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        //送给kafka
        eventProducer.fireEvent(event);


        return "redirect:/discuss/detail/" + discussPostId;
    }
}
