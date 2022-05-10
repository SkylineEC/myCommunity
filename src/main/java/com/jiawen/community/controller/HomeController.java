package com.jiawen.community.controller;


import com.jiawen.community.entity.DiscussPost;
import com.jiawen.community.entity.Page;
import com.jiawen.community.entity.User;
import com.jiawen.community.service.DiscussPostService;
import com.jiawen.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;



    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        //System.out.println("Page对象作为参数传入时，page.getCurrent() = " + page.getCurrent());
        //当传入路径参数current的时候  由于函数规定有两个参数SpringBoot会自动查找第二个参数page 里面有没有current的参数
        //如果有就会自动赋值给page.current
        //page设置总行数
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");


        List<DiscussPost> list =  discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());

        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if (list!=null){
            for (DiscussPost post : list){
                HashMap<String, Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);

        //这一步可以省略 在SpringMVC 方法参数可以被初始化 会自动注入Page对象给model
        //实际上可以通过Model获得Page
        //model.addAttribute("page",page);
        //在thymeleaf里面可以直接通过model访问page对象
        return "index";

    }

}
