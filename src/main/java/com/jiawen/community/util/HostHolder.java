package com.jiawen.community.util;


import com.jiawen.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    //主要起到容器的作用 持有用户的信息 用于代替session对象
    //做到线程隔离的效果
    private ThreadLocal<User> users = new ThreadLocal<User>();


    //ThreadLocal 实现线程隔离
    //以线程为key存值到一个Map里面

    public void setUser(User user){
        users.set(user);

    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
