package com.jiawen.community.event;

import com.alibaba.fastjson.JSONObject;
import com.jiawen.community.entity.Event;
import com.jiawen.community.entity.Message;
import com.jiawen.community.service.MessageService;
import com.jiawen.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: myCommunity
 * @description:
 * @author: Mr.Wang
 * @create: 2022-06-02 14:24
 **/
@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    //向message表里面插入数据
    @Autowired
    private MessageService messageService;

    //一个方法处理三种主题
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_FOLLOW,TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("消息内容为空");

        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null){
            logger.error("消息格式错误");
        }
        //内容格式都没有问题 就发一个站内信 后台向用户发消息的时候因为是系统消息 发件人默认为1
        //所以conversation_id字段是主题
        Message message = new Message();


        //message包含基础数据和内容
        //基础数据👇
        //1就是系统用户
        message.setFromId(SYSTEM_USER_ID);
        //张三给李四点赞 应该通知李四 所以应该从event里面取entityUserId
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());



        //要设置content 用户xxx(A B C...) xxx(点赞 评论) 了你的 xxx(帖子 评论)
        Map<String,Object> content = new HashMap<>();
        //事件是谁触发的
        content.put("userId",event.getUserId());
        //实体的类型
        content.put("entityType",event.getEntityType());
        //实体的id
        content.put("entityId",event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);

        //系统用户的id
    }
}
