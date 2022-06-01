package com.jiawen.community.service;

import com.jiawen.community.dao.LoginTicketMapper;
import com.jiawen.community.dao.UserMapper;
import com.jiawen.community.entity.LoginTicket;
import com.jiawen.community.entity.User;
import com.jiawen.community.util.CommunityConstant;
import com.jiawen.community.util.CommunityUtil;
import com.jiawen.community.util.MailClient;
import com.jiawen.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


//所有跟user有关的操作

@Service
public class UserService implements CommunityConstant {
   //注入用户Mapper
   @Autowired
   private UserMapper userMapper;

   @Autowired
   private MailClient mailClient;

//   @Autowired
//   private LoginTicketMapper loginTicketMapper;

   //注入模板引擎
   @Autowired
   private TemplateEngine templateEngine;

   //激活码配置域名和项目名
   @Value("${community.path.domain}")
   private String domain;

   @Value("${server.servlet.context-path}")
   private String contextPath;

   @Autowired
   private RedisTemplate redisTemplate;

   public User findUserByEmail(String email){
       return userMapper.selectByEmail(email);
   }
    public User findUserById(int id) {
        //从cache里面查
        User user = getCache(id);
        if (user == null){
            user = initCache(id);
        }
        return user;
    }

    public User findUserByName(String name){
       return userMapper.selectByName(name);
    }
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
           return map;
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

   public Map<String,Object> sendForgetEmail(String email){
       User user = userMapper.selectByEmail(email);
       Map<String,Object> map = new HashMap<>();
       if(user == null){
           map.put("emailMsg","邮箱不存在");
           return map;
       }

       Context context = new Context();
       context.setVariable("username",user.getUsername());
       String code = CommunityUtil.generateUUID().substring(6);
       context.setVariable("code",code);
       String content = templateEngine.process("mail/forget",context);
       mailClient.sendMail(user.getEmail(),"MyCommunity Reset Password",content);
       map.put("code",code);
       return map;
   }


   public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
   }


   /*
   按照MD5进行加密 如果加密后的密码和MYSQL里面存的密码一致 就验证通过
    */
   public Map<String,Object> login(String username, String password,int expiredSeconds){
       Map<String,Object> map = new HashMap<>();
       //空值处理
       if(StringUtils.isBlank(username)){
           map.put("usernameMsg","账号不能为空");
           return map;

       }
       if(StringUtils.isBlank(password)){
           map.put("passwordMsg","密码不能为空");
           return map;

       }


       //进行合法性验证
       //输入username查一下看有没有
       //然后看一下密码是否一致
       User user = userMapper.selectByName(username);
       if(user == null){
           map.put("usernameMsg","账号不存在");
           return map;
       }
       if(user.getStatus() == 0){
           map.put("usernameMsg","账号未激活");
           return map;

       }
       //说明账号存在并且激活
       //验证密码
       //对传入的明文密码进行加密
       password = CommunityUtil.md5(password + user.getSalt());
       if(!user.getPassword().equals(password)){
           map.put("passwordMsg","密码不正确");
           return map;
       }

       //生成登录凭证 服务器要记录
       LoginTicket loginTicket = new LoginTicket();
       loginTicket.setUserId(user.getId());
       //Ticket就是一个随机字符串
       loginTicket.setTicket(CommunityUtil.generateUUID());
       loginTicket.setStatus(0);
       loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
       //loginTicketMapper.insertLoginTicket(loginTicket);
//       登录成功之后ticket保存到redis当中
       String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
       //redis会自动把对象序列化一个json格式的字符串
       redisTemplate.opsForValue().set(redisKey,loginTicket);


       //最后需要把凭证发给客户端
       //浏览器只需要记录一个key(登录凭证)
       //下次登陆的时候  如果服务器对上了LoginTicket 就获取了userID
       map.put("ticket",loginTicket.getTicket());
       return map;

   }

   public void logout(String ticket){
       //需要把凭证传过来给服务端
       String redisKey = RedisKeyUtil.getTicketKey(ticket);
       LoginTicket loginTicket  = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
       loginTicket.setStatus(1);
       redisTemplate.opsForValue().set(redisKey,loginTicket);
//       loginTicketMapper.updateStatus(ticket,1);


   }

   public Map<String,Object> forgetAndResetPassword(String email, String realCode, String verifyCode, String password){
       Map<String,Object> map =  new HashMap<>();
       if(email == null){
           map.put("emailMsg","邮箱为空");
           return map;
       }
       if(realCode == null){
           map.put("codeMsg","会话中没有验证码");
           return map;
       }
       if(verifyCode == null){
           map.put("codeMsg","输入验证码为空");
           return map;
       }

       if(!verifyCode.equals(realCode)){
           map.put("errorMsg","验证码错误,请重新输入");
           return map;
       }else{
           User user = findUserByEmail(email);
           password = CommunityUtil.md5(password + user.getSalt());
           userMapper.updatePassword(user.getId(),password);
           clearCache(user.getId());
       }


       return map;

   }

   //通过ticket凭证查询代码
    public LoginTicket findLoginTicket(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }


    //返回更新的行数
    public int updateHeader(int userId, String headerUrl){

       int rows = userMapper.updateHeader(userId,headerUrl);
        clearCache(userId);
        return rows;
    }


    /*
    缓存：每次先尝试从缓存当中取值
    每次更改用户数据之后就更新 或者直接将缓存删除 下一次请求重查
    1. 当查询时 优先从缓存中取值
    2. 如果没有取到 初始化缓存数据
    3. 数据变更时清除缓存数据
     */
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    //如果取不到
    private User initCache(int userId){
        //初始化缓存需要从缓存中查
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        //一个小时
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);

        return user;
    }

    //数据变更的时候 需要清除缓存
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }


}
