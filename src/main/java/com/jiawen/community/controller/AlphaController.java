package com.jiawen.community.controller;


import com.jiawen.community.service.AlphaBeanService;
import com.jiawen.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    AlphaBeanService alphaBeanService;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "Hello";
    }

    @RequestMapping("/hello_1")
    @ResponseBody
    public String select(String str){
        return this.alphaBeanService.select(str);
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try (
                PrintWriter writer = response.getWriter();
        ) {
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // RequestMapping 可以指定路径也可以指定方法
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            //RequestParam浏览器向服务器传参有两种方式，
            // 一是在通过get请求，在路径后加问号携带参数，
            // 如/xxx?id=1。另一种是通过post请求，
            // 在request请求体中携带表单中的参数，这种参数在路径上是看不到的。
            // 这两种方式所传的参数，在服务端都可以通过request.getParameter(参数名)这样的方式来获取。
            // 而@RequestParam注解，就相当于是request.getParameter()，
            // 是从request对象中获取参数的。
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // 当变量被{}括起来的时候 就是path var
    @RequestMapping(path = "/student/{id}/{name}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id,
                             @PathVariable("name") String name
    ) {
        System.out.println(id);
        System.out.println(name);
        return "a student";
    }

    // POST请求
    //通常用于提交数据，比如提交表单，提交文件等。
    //http://localhost:8080/html/students.html
    //中提交表单, saveStudnet的参数表对应表单左右input的name属性 名称对应就可以自动传过来

    @RequestMapping(path = "/student_add", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //如何向浏览器返回响应数据
    //向浏览器响应一个动态的html
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    //不加注解通常响应的是一个静态的html文件
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张三");
        mav.addObject("age", 30);
        //设置一个返回的模板 因为spring默认的返回页面是html 所以view后面不加后缀
        mav.setViewName("/demo/view");
        return mav;
    }


    //向浏览器·响应html数据的第二种办法
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", 80);
        return "/demo/view";
    }

    //在异步数据中通常响应json数据
    //当前网页不刷新 但是悄悄返回了一个结果
    //比如注册用户过程 后台要查用户名是否被占用

    //java -> json -> js对象
    //json起了衔接的作用
    //浏览器会自动响应json请求

    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", 24);
        emp.put("salary", 9000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", 25);
        emp.put("salary", 10000.00);
        list.add(emp);

        return list;
    }

    //cookie 模拟浏览器访问服务器 服务器创建cookie
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //cookie写道response里面 返回给浏览器
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //指定访问路径 有的路径才需要发cookie
        //只有在这个路径还有子路径下面才有效
        cookie.setPath("community/alpha");
        //设置的cookie的生存时间 十分钟
        cookie.setMaxAge(60*10);

        response.addCookie(cookie);

        return "set cookie";


    }

    //当浏览器访问服务器的时候 服务器会创建一个session 向浏览器返回一个cookie(里面包含session ID)
    //浏览器会存cookie对象
    //下次访问的时候拿着这个session ID 去访问服务器 这样就可以取到服务器的session
    //下面是一个session的示例
    //创建一个session Spring MVC可以自动创建Session 只要声明就可以注入
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        //session可以存任何类型的数据 cokkie只可以存字符串
        session.setAttribute("id",1);
        session.setAttribute("name","Wang Jiawen");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("name"));
        System.out.println(session.getAttribute("id"));
        return "get session";
    }
}
