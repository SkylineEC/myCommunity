package com.jiawen.community.service;

import com.jiawen.community.CommunityApplication;
import com.jiawen.community.dao.UserMapper;
import com.jiawen.community.entity.User;
import com.jiawen.community.util.CommunityConstant;
import com.jiawen.community.util.CommunityUtil;
import com.jiawen.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.xml.transform.Templates;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


//所有跟user有关的操作

@Service
public class UserService implements CommunityConstant {
   //注入用户Mapper
   @Autowired
   private UserMapper userMapper;

   @Autowired
   private MailClient mailClient;


   //注入模板引擎
   @Autowired
   private TemplateEngine templateEngine;

   //激活码配置域名和项目名
   @Value("${community.path.domain}")
   private String domain;

   @Value("${server.servlet.context-path}")
   private String contextPath;

   //返回的信息具有多种情况
   public Map<String,Object> register(User user){

       Map<String,Object> map = new HashMap<>();

       //对参数进行判断  空值处理
       if(user == null){
           throw new IllegalArgumentException("参数不能为空");
       }
       //如果账户是空的
       if(StringUtils.isBlank(user.getUsername())){
           map.put("usernameMsg","用户名不能为空");
           return map;
       }

       if(StringUtils.isBlank(user.getPassword())){
           map.put("passwordMsg","密码不能为空");
           return map;

       }
       if(StringUtils.isBlank(user.getEmail())){
           map.put("emailMsg","邮箱不能为空");
       }



       //判断用户名是否存在
       User userFromDatabse = userMapper.selectByName(user.getUsername());
       if(userFromDatabse != null){

          //如果查到
           map.put("usernameMsg","用户名已存在");
           return map;
       }

       //验证邮箱是否存在
       userFromDatabse = userMapper.selectByEmail(user.getEmail());
       if(userFromDatabse != null){
           map.put("emailMsg","邮箱已存在");
       }

       //账号密码邮箱都不为空 并且账号 邮箱都不存在
      //注册用户

      //对密码加密 加一点盐 随机产生一个字符串
      user.setSalt(CommunityUtil.generateUUID().substring(0,5));

      //加密
      user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));


      user.setType(0);
      user.setStatus(0);
      user.setActivationCode(CommunityUtil.generateUUID());
      //给用户一个随机的头像
      user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
      //给user设置一个注册的时间
      user.setCreateTime(new Date());


      //添加到库里面
      userMapper.insertUser(user);


      //发送激活邮件 我们选择mail/activation.html
       Context context = new Context();
       context.setVariable("email",user.getEmail());
       context.setVariable("username",user.getUsername());
       //localhost:8080/community/activation/user_id/activation_code
       //当insert之后 user就会有id了 SpringBoot会自动回填
       String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
       context.setVariable("activationCode",user.getActivationCode());
       context.setVariable("url",url);
//       拼好条件之后 就可以用模板引擎生成邮件内容
       String content = templateEngine.process("mail/activation",context);
       mailClient.sendMail(user.getEmail(),"MyCommunity Activation",content);

       return map;

   }
   public User findUserById(int id){
           return userMapper.selectById(id);
       }
   public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
   }

}
