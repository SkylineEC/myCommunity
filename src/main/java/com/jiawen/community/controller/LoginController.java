package com.jiawen.community.controller;


import com.google.code.kaptcha.Producer;
import com.jiawen.community.entity.User;
import com.jiawen.community.service.UserService;
import com.jiawen.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;


    @RequestMapping(path = "register",method = {RequestMethod.GET})
    public String getRegisterPage() {
        return "/site/register";
    }

    @RequestMapping(path = "login",method = {RequestMethod.GET})
    public String getLoginPage() {
        return "/site/login";
    }

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //定义方法来注册请求，一定是POST请求
    @RequestMapping(path = "register",method = {RequestMethod.POST})
    //传入的时候直接生成一个user对象
    //只要html页面传入值的时候 值和User属性相匹配 那么SpringBoot就会自动装配到User
    public String register(Model model, User user) {
       Map<String,Object> map = userService.register(user);
       if(map == null || map.isEmpty()) {
           //注册成功
           //跳转到登录页面
           model.addAttribute("msg","注册成功，我们已经向你的邮箱发送了一封激活邮件");
           model.addAttribute("target","/index");
           return "/site/operate-result";

       }else {
           //注册失败 账号 密码 邮箱 有问题

           //不知道是哪个问题 但是都发过去
           model.addAttribute("usernameMsg",map.get("usernameMsg"));
           model.addAttribute("passwordMsg",map.get("passwordMsg"));
           model.addAttribute("emailMsg",map.get("emailMsg"));
           //把这三个消息发送回页面
           return "/site/register";

       }


    }

    //localhost:8080/community/activation/user_id/activation_code
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("code") String code
    ) {
        int result = userService.activation(userId, code);
        if(result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了");
        }
        else if(result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过了");
        }
        else {
            model.addAttribute("msg", "无效操作，激活码错误");
        }
        return "/site/operate-result";

    }


    //生成验证码的时候不能存在浏览器端 属于是敏感信息
    //我们可以存到Session当中
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //验证码存储到session当中
        session.setAttribute("kaptcha", text);
        //将图片输出给浏览器
        //声明给浏览器返回的是什么格式的数据
        response.setContentType("image/png");
        //设置响应头
        try{
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);


        }
        catch (Exception e) {
            logger.error("生成验证码失败" + e.getMessage());
        }

    }


    //我要处理表单提交的数据 就要用Post
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username,
                        String password,
                        String code ,
                        Boolean rememberme,
                        Model model,
                        HttpSession session,//验证码放到了session里面 需要从session把验证码取出来 如果登录成功了 需要ticket发放给客户端进行保存
                        HttpServletResponse response){
        //首先要判断验证码对不对
        //之前session就设置了验证码
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            //验证码不区分大小写equalsIgnoreCase
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }
        //检查账号 密码
        //判断是否用户勾上 记住我
        int expiredSeconds = rememberme? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        System.out.println(expiredSeconds);
        Map<String,Object> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            //把ticket取出来 发送给客户端 让客户端存 也就是给客户端发送一个cookie 带上ticket
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            //设置cookie有效时间
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);//服务端有cookie之后 就可以给浏览器了


            //重定向到首页index
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            //如果不对 没有ticket
            return "/site/login";
        }

    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        //首先浏览器要发送之前存的cookie给服务器
        //浏览器是自动存储cookie的 所以这时候浏览器需要将cookie发送给服务器
        //我们可以使用@CookieValue将cookie注入进去
        userService.logout(ticket);
        return "redirect:/login"; //重定向的时候 默认是GET请求
        //下面一步是去前端配置超链接
        //点击 "退出登录 " 就触发方法

    }
    @RequestMapping(path = "/forget" , method = RequestMethod.GET)
    public String forget(Model model){
        return "site/forget";
    }

    @RequestMapping(path = "/forgotAndSendEmail/{email}", method = RequestMethod.GET)

    public String forgotAndSendEmail(Model model,
                                     HttpSession session,
                                     @PathVariable("email") String email
    ){
        Map<String,Object> map = userService.sendForgetEmail(email);
        if(map.containsKey("emailMsg")){
            session.setAttribute("email",email);

            model.addAttribute("emailMsg","邮箱不存在");
            return "/mail/forget";
        }else {
            //如果邮箱存在
            String code = (String) map.get("code");
            session.setAttribute("email",email);
            session.setAttribute("code",code);
        }

        return "/site/forget";

    }

    @RequestMapping(path = "/resetPassword", method = RequestMethod.POST)
    public String resetPassword(Model model,
                                HttpSession session,
                                HttpServletRequest request){

        String password =request.getParameter("your-password");
        String verifyCode =request.getParameter("verifycode");
        String realCode = (String) session.getAttribute("code");
        String email = (String)session.getAttribute("email");
        Map<String,Object> map = userService.resetPassword(email,realCode,verifyCode,password);
        if(map.size() == 0){
            //修改密码成功
            return "site/login";
        }else{
            model.addAttribute("codeMsg", map.get("codeMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("errorMsg", map.get("errorMsg"));
            return "site/forget";
        }



    }




}
