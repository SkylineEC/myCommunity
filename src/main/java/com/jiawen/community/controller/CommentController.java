package com.jiawen.community.controller;

import com.jiawen.community.entity.Comment;
import com.jiawen.community.service.CommentService;
import com.jiawen.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    //我希望在评论完重定向到本页面 但是本页面路径是包含帖子id的 所以我们要获取贴子id
    @RequestMapping(path = "/add/{discussPostId}" ,method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussPostId,
                             Comment comment
                             ){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
