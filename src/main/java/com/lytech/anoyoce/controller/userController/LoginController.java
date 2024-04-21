package com.lytech.anoyoce.controller.userController;


import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 吴嘉豪
 * @date 2023/11/14 16:36
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class LoginController {
    @Autowired
    private LoginService loginService;
    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user) {
        // 登录
        // Authenticate 进行认证

        // 如果认证没有通过

        // 如果认证通过， 使用userid 生成一个jwt

        // 将信息存入到redis
        return loginService.login(user);
    }
    @RequestMapping("/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }
}
