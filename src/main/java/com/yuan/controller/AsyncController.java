package com.yuan.controller;


import com.yuan.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
