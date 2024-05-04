package com.lytech.anoyoce.controller;

import cn.hutool.core.util.StrUtil;
import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.domain.vo.UserInfo;
import com.lytech.anoyoce.service.UserService;
import com.lytech.anoyoce.utils.GetLoginUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * 查询用户信息
     * @return
     */
    @GetMapping("/query/userInfo")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult queryUser(@RequestParam(value = "userId", required = false) String userId){
        if(StrUtil.isEmpty(userId)){
            userId = GetLoginUserUtils.getUserId().toString();
        }
        UserInfo userInfo = userService.getUserInfo(Long.valueOf(userId));
        return ResponseResult.success(userInfo);
    }
}
