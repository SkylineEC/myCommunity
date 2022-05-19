package com.jiawen.community.controller;


import com.jiawen.community.entity.Comment;
import com.jiawen.community.entity.DiscussPost;
import com.jiawen.community.entity.Page;
import com.jiawen.community.entity.User;
import com.jiawen.community.service.CommentService;
import com.jiawen.community.service.DiscussPostService;
import com.jiawen.community.service.UserService;
import com.jiawen.community.util.CommunityConstant;
import com.jiawen.community.util.CommunityUtil;
import com.jiawen.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {


    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

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

    //只要一共SpringMVC托管的Java Bean在参数列表当中 SpringBoot会将它存在Model里面
    //所以这里model会获取page
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
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
        //评论的分页信息

        //每页显示五条评论 可以看到分页的效果
        page.setLimit(5);

        page.setPath("/discuss/detail/" + discussPostId);

        page.setRows(post.getCommentCount());

        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST,post.getId(),page.getOffset(),page.getLimit()
        );

        //评论数据中有User ID 需要获得User显示 名字 头像
        List<Map<String, Object>> commentVoList = new ArrayList<>();

        //评论: 给帖子的评论
        //回复: 给评论的评论

        //评论的Vo列表
        if(commentList != null){
            for (Comment comment : commentList){
                //一个评论的Vo
                Map<String, Object> commentVo = new HashMap<>();
                //向Vo中添加评论
                commentVo.put("comment",comment);
                //给评论添加作者信息
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                //回复的Vo列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply : replyList){
                        //每次遍历得到的数据都装到Vo里面
                        Map<String,Object> replyVo = new HashMap<>();
                        //向map中存回复
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));

                        //处理targetID
                        //查到回复目标的User 普通的评论没有目标 只有回复的时候有目标
                        //所以要判断targetID是不是0
                        //帖子的评论不用这样的处理 因为帖子的评论没有指向性的回复
                        //targetID只发生在A回复B里面 评论的评论的回复
                        User target = reply.getTargetId() == 0? null : userService.findUserById(reply.getTargetId());
                        //这样查询得到了目标的用户
                        replyVo.put("target",target);



                        replyVoList.add(replyVo);
                    }
                }

                commentVo.put("replys",replyVoList);

                //还要再补充一个内容
                //要统计每一个评论的回复数量

                //回复的数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments",commentVoList);


        //TODO: 帖子还有回复功能 所以先不查询
        return "/site/discuss-detail";
    }
}
