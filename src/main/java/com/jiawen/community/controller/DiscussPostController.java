package com.jiawen.community.controller;


import com.jiawen.community.entity.DiscussPost;
import com.jiawen.community.entity.User;
import com.jiawen.community.service.DiscussPostService;
import com.jiawen.community.service.UserService;
import com.jiawen.community.util.CommunityUtil;
import com.jiawen.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {


    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtil.getJsonString(403,"你还没有登录哦");

        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        return CommunityUtil.getJsonString(0,"发布帖子成功");
        //如果程序报错 将来会统一处理
    }


    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        //帖子查到就好了
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        //传给模板
        model.addAttribute("post",post);
        //要对用户的id做处理 要显示用户的具体信息
        /**
         * 1. 可以进行MyBatis关联查询 但是查询方法会有耦合
         * 2. 可以进行两次查询获得User 但是查两次效率低一点
         */
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //TODO: 帖子还有回复功能 所以先不查询
        return "/site/discuss-detail";
    }
}
