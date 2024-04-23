package com.lytech.anoyoce.controller;

import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user) {
        return userService.login(user);
    }
    @PostMapping("/logout")
    public ResponseResult logout(){
        return userService.logout();
    }
    @PutMapping("/register")
    public ResponseResult<Boolean> register(@RequestBody User user) {
        String password = user.getPassword();
        String userName = user.getUserName();
        boolean register = userService.register(password, userName);
        return ResponseResult.success(register);
    }

}
