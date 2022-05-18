package com.jiawen.community.controller;


import com.jiawen.community.annotation.LoginRequired;
import com.jiawen.community.entity.User;
import com.jiawen.community.service.UserService;
import com.jiawen.community.util.CommunityUtil;
import com.jiawen.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


/*
这个controller处理用户有关的逻辑
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    //更新当前用户图像 需要从HostHolder中取得用户
    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }


    //上传文件只需要在表现层处理就好了 MultipartFile属于是表现层的部分
    //我们上传文件的过程中 需要用到域名 项目名
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    //使用SpringMVC提供的专有类型来提交
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("errorMsg","您还没有选择图片");
            return "/site/setting";
        }else {
            //开始上传图片
            //图片名字得重新随机生成
            String fileName = headerImage.getOriginalFilename();
            //寻找最后一个. 的索引
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            if(StringUtils.isBlank(suffix)){
                model.addAttribute("error","文件格式不正确");
                return "/site/setting";
            }

            fileName = CommunityUtil.generateUUID() + suffix;
            //确定一个文件存放的路径 才可以存
            File dest = new File(uploadPath + "/" + fileName);
            //目前dest 是空的 需要把headerImage的内容写入这个dest
            try {
                headerImage.transferTo(dest);
            } catch (IOException e) {
                logger.error("上传文件失败" + e.getMessage());
                throw new RuntimeException("上传文件失败, 服务器发生异常",e);
            }
            //如果存成功了 更新当前用户头像的路径 // 需要提供web访问路径(可以通过浏览器访问)
            // http://localhost:8080/community/user/header/xxx.png xx是随机的字符串
            User user = hostHolder.getUser();
            //允许外界访问的web路径
            String headerUrl = domain + contextPath + "/user/header/" + fileName;
            userService.updateHeader(user.getId(),headerUrl);

            //重定向到首页 刷新页面
            return "redirect:/index";
        }
    }


    //它向浏览器访问一个二进制的图片 手动调用response往外写
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName,
                          HttpServletResponse response){

        //首先通过fileName找到存在服务器本地的文件名字
        fileName = uploadPath + "/" + fileName;

        //要向浏览器输出图片 输出的时候要声明文件的格式(后缀)
        //解析后缀获取文件名最后一个点
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);
        //使用字节流
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os =  response.getOutputStream();
                ) {
            //建立缓冲区
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取图像失败 " + e.getMessage());
        }





    }



}
