package com.lytech.anoyoce.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    // 自定义el表达式 @ex 获取容器中定义的名字

    @PreAuthorize("@ex.hasAuthority('system:dept:list')")
    public String hello(){
        return "hello";
    }
}

