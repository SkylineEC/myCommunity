package com.jiawen.community;


import com.jiawen.community.dao.UserMapper;
import com.jiawen.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

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


}
