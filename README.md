---
title: 异步、邮件、定时任务
date: 2020-05-14 18:43:10
tags:
	- 异步任务
	- 邮件任务
	- 定时任务
categories:
	- SpringBoot
---



# 1. 异步任务

## 1.1 背景

很多工作要我们异步完成，比如发送邮件，我们不可能等到邮件发送完毕才回馈用户，又或者前台支付，我们不需要等到前台收到汇款之后，自己的手机才可以进行下一步操作。SpringBoot为我们的异步任务提供了支持，只需要配置几个注解即可。



## 1.2 SpringBoot整合

1. Controller

```java
@RestController
public class AsyncController {

    private AsyncService asyncService;

    public AsyncController(AsyncService asyncService) {
        this.asyncService = asyncService;
    }

    @RequestMapping("/hello")
    public String hello(){
        asyncService.hello();//停止三秒
        return "OK";
    }
}
```

我们controller调用service的方法，但是service方法没有开启异步：

2. service

```java
//异步任务
@Service
public class AsyncService {

    //告诉spring这是一个异步的方法
    //@Async
    public void hello(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("数据正在处理");
    }
    
}
```

这里我们没有对这个方法开启异步，要等待3s钟，也就是说前台访问页面要在3s后才可以打出“OK”

![image-20200514185453624](https://i.loli.net/2020/05/14/z2SDCgnBy85iAoZ.png)

**3s后：**

![image-20200514185506810](https://i.loli.net/2020/05/14/dkD1EmqMAf2KbY6.png)

## 1.3 实现异步任务只需要两步

1. 在SpringBoot的**主启动类上加上@EnableAsync注解**。开启异步功能

![image-20200514185541559](https://i.loli.net/2020/05/14/gBAqPF87lnXHr4Q.png)

2. 在**service的对应方法上加上@Async**，标志这是一个异步方法

![image-20200514185644496](https://i.loli.net/2020/05/14/dNuZyx91n2lH48s.png)

3. 测试

这样我们的访问页面的时候，页面会直接返回`“ok”`，3s后，控制台打印出`“数据正在处理”`。



# 2. 邮件任务

邮件发送，在我们的日常开发中，也非常的多，Springboot也帮我们做了支持

- 邮件发送需要引入spring-boot-start-mail
- SpringBoot 自动配置MailSenderAutoConfiguration
- 定义MailProperties内容，配置在application.yml中
- 自动装配JavaMailSender
- 测试邮件发送



## 2.1 引入pom依赖

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```



## 2.2 查看自动配置类： MailSenderAutoConfiguration

![image-20200514191510986](https://i.loli.net/2020/05/14/hvKFCRwnNoP5S9D.png)

在自动配置类中并没有Bean，我们去看一下他的导入类

![image-20200514191630471](https://i.loli.net/2020/05/14/fWnjOa8hQXCltsk.png)

我们再去看一下MailProperties：

![image-20200514191738810](https://i.loli.net/2020/05/14/8MBqtdsrHTmUfZ4.png)

可配置的属性大概有如下这一些，在application.yml的配置文件中前追问spring.mail



## 2.3 配置文件

```properties
server.port=11111
spring.mail.username=yuantb@yeah.net
spring.mail.password=JGBXWBTSGNLIJRYR
spring.mail.host=smtp.yeah.net
```

其中password需要我们在自己的邮箱中开启pop3和smtp服务



## 2.4 Spring单元测试

![image-20200514192029279](https://i.loli.net/2020/05/14/klz62FnWLbafu5I.png)

具体的邮件发送需要我们使用JavaMailSenderImpl类，然后邮件类型有：

```java
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
```

简单邮件的话，直接新建SimpleMailMessage即可，然后编写主体和内容，选择寄件人和收件人即可，复杂邮件的话，可以选择支持html样式，通过File添加附件，最后同样由JavaMailSenderImpl进行发送。



# 3. 定时任务

项目开发中经常需要执行一些定时任务，比如需要在每天凌晨的时候，分析一次前一天的日志信息，Spring为我们提供了定时执行任务调度的方式，提供了两个接口。

- TaskExecutor接口
- TaskScheduler接口

两个注解：

- @EnableScheduling
- @Scheduled

**cron表达式：**

![image-20200514192604888](https://i.loli.net/2020/05/14/YNuXtQHMmRJGqnC.png)

**测试步骤：**

1、创建一个ScheduledService

我们里面存在一个hello方法，他需要定时执行，怎么处理呢？

```java
@Service
public class ScheduledService {
   
   //秒   分   时     日   月   周几
   //0 * * * * MON-FRI
   //注意cron表达式的用法；
   @Scheduled(cron = "0 * * * * 0-7")
   public void hello(){
       System.out.println("hello.....");
  }
}
```

2、这里写完定时任务之后，我们需要在主程序上增加@EnableScheduling 开启定时任务功能

```java
@EnableAsync //开启异步注解功能
@EnableScheduling //开启基于注解的定时任务
@SpringBootApplication
public class SpringbootTaskApplication {

   public static void main(String[] args) {
       SpringApplication.run(SpringbootTaskApplication.class, args);
  }

}
```

3、我们来详细了解下cron表达式；

http://www.bejson.com/othertools/cron/

4、常用的表达式

```
（1）0/2 * * * * ?   表示每2秒 执行任务
（1）0 0/2 * * * ?   表示每2分钟 执行任务
（1）0 0 2 1 * ?   表示在每月的1日的凌晨2点调整任务
（2）0 15 10 ? * MON-FRI   表示周一到周五每天上午10:15执行作业
（3）0 15 10 ? 6L 2002-2006   表示2002-2006年的每个月的最后一个星期五上午10:15执行作
（4）0 0 10,14,16 * * ?   每天上午10点，下午2点，4点
（5）0 0/30 9-17 * * ?   朝九晚五工作时间内每半小时
（6）0 0 12 ? * WED   表示每个星期三中午12点
（7）0 0 12 * * ?   每天中午12点触发
（8）0 15 10 ? * *   每天上午10:15触发
（9）0 15 10 * * ?     每天上午10:15触发
（10）0 15 10 * * ?   每天上午10:15触发
（11）0 15 10 * * ? 2005   2005年的每天上午10:15触发
（12）0 * 14 * * ?     在每天下午2点到下午2:59期间的每1分钟触发
（13）0 0/5 14 * * ?   在每天下午2点到下午2:55期间的每5分钟触发
（14）0 0/5 14,18 * * ?     在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
（15）0 0-5 14 * * ?   在每天下午2点到下午2:05期间的每1分钟触发
（16）0 10,44 14 ? 3 WED   每年三月的星期三的下午2:10和2:44触发
（17）0 15 10 ? * MON-FRI   周一至周五的上午10:15触发
（18）0 15 10 15 * ?   每月15日上午10:15触发
（19）0 15 10 L * ?   每月最后一日的上午10:15触发
（20）0 15 10 ? * 6L   每月的最后一个星期五上午10:15触发
（21）0 15 10 ? * 6L 2002-2005   2002年至2005年的每月的最后一个星期五上午10:15触发
（22）0 15 10 ? * 6#3   每月的第三个星期五上午10:15触发
```