package com.jiawen.community.service;


import com.jiawen.community.controller.AlphaController;
import com.jiawen.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
// 多实例模式(Spring默认单例)
// @Scope("prototype")
public class AlphaBeanService {


    @Qualifier("impl2")
    @Autowired
    AlphaDao alphaDao;

    public AlphaBeanService(){
        System.out.println("Constructor...");
    }


    @PostConstruct
    public void init(){
        System.out.println("Init...");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("Destroy...");
    }

    public String select(String str){
        return alphaDao.select(str);
    }
}
