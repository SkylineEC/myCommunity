package com.jiawen.community;

import com.jiawen.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

    @Test
    public void test() {
        mailClient.sendMail("tang0319jia@gmail.com", "test:来自于jiawen的社区", "你好 这篇邮件来自于jiawen的社区");
    }

    @Test
    public void testHtml() {
        Context context = new Context();
        context.setVariable("username", "jiawen");
        String content = templateEngine.process("/mail/demo", context);
        mailClient.sendMail("e-wjg@outlook.com", "test:来自于jiawen的社区", content);
    }


}
