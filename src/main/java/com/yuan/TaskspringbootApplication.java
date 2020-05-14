package com.yuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableAsync//开启异步
@SpringBootApplication
@EnableScheduling//开启定时功能
public class TaskspringbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskspringbootApplication.class, args);
    }

}
