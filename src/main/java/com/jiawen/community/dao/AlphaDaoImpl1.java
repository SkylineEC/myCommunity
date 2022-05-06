package com.jiawen.community.dao;


import org.springframework.stereotype.Repository;



@Repository("impl1")
public class AlphaDaoImpl1 implements AlphaDao{

    @Override
    public String select() {
        return "Implementation 1";
    }
}
