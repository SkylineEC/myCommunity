package com.jiawen.community.event;

import com.alibaba.fastjson.JSONObject;
import com.jiawen.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @program: myCommunity
 * @description:
 * @author: Mr.Wang
 * @create: 2022-06-02 14:21
 **/
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event){

        //将事件发布到指定的主题

        //将对象转为json
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));



    }

}
