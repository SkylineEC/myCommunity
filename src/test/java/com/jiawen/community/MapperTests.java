package com.jiawen.community;


import com.jiawen.community.dao.DiscussPostMapper;
import com.jiawen.community.dao.LoginTicketMapper;
import com.jiawen.community.dao.UserMapper;
import com.jiawen.community.entity.DiscussPost;
import com.jiawen.community.entity.LoginTicket;
import com.jiawen.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public User testSelectById(int id) {
        User user = userMapper.selectById(id);

        System.out.println(userMapper.selectByEmail("nowcoder102@sina.com"));
        return user;
    }

    @Test
    public void insertUser() {
        User user = new User();
        user.setUsername("测试用户");
        user.setPassword("123456");
        user.setEmail("e-mail@hotmail.com");
        user.setSalt("salt");
        user.setHeaderUrl("http://www.nowcoder.com/head/007.png");
        user.setCreateTime(new Date());
        user.setType(0);
        user.setStatus(0);
        userMapper.insertUser(user);

        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> postList = discussPostMapper.selectDiscussPosts(103,1,10);
        for(DiscussPost post : postList) {
            System.out.println(post);
        }
        System.out.println(discussPostMapper.selectDiscussPostRows(103));
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("ABC");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("abc", 1);

        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }


}
