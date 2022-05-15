package com.jiawen.community.controller;


import com.google.code.kaptcha.Producer;
import com.jiawen.community.entity.User;
import com.jiawen.community.service.UserService;
import com.jiawen.community.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
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
}
