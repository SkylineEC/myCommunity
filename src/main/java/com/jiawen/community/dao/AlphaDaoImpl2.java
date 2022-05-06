package com.jiawen.community.dao;


import org.springframework.stereotype.Repository;

@Repository("impl2")
public class AlphaDaoImpl2 implements AlphaDao{
    @Override
    public String select() {
        return "Implementation 2";
    }
}
