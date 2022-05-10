package com.jiawen.community.service;

import com.jiawen.community.dao.UserMapper;
import com.jiawen.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//所有跟user有关的操作

@Service
public class UserService {
   //注入用户Mapper
   @Autowired
   private UserMapper userMapper;

   public User findUserById(int id){
       return userMapper.selectById(id);
   }



}
