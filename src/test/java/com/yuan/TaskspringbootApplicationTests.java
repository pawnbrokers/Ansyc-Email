package com.yuan;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@SpringBootTest
class TaskspringbootApplicationTests {

    @Autowired
    JavaMailSenderImpl javaMailSender;
    @Test
    void contextLoads() throws MessagingException {


//        //一个简单地邮件
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setSubject("袁堂波你好啊");
//        message.setText("这是第一篇邮件");
//        message.setTo("1073617063@qq.com");
//        message.setFrom("yuantb@yeah.net");
//        javaMailSender.send(message);



        //复杂的邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        File file = new File("C:\\Users\\yuan\\Desktop\\pics\\2.jpg");
        System.out.println(file.hashCode());
        //组装
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
        helper.setSubject("老博你好啊Plus");
        helper.setText("<p style='color:red'>这是plus邮件</p>",true);
        helper.addAttachment("2.jpg",file);
        helper.setTo("911956918@qq.com");
        helper.setFrom("yuantb@yeah.net");
        javaMailSender.send(mimeMessage);
    }

}
