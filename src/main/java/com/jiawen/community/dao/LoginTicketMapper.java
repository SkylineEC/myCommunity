package com.jiawen.community.dao;

import com.jiawen.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;


@Mapper
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket (user_id, ticket, status, expired) ",
            "values (#{userId}, #{ticket}, #{status}, #{expired}) "
    })
    //指定自增主键
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    @Select(
            {"select * from login_ticket where ticket=#{ticket}"}
    )
    LoginTicket selectByTicket(String ticket);


    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);


}


