package com.lytech.anoyoce.service;


import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.User;

/**
 * @author 吴嘉豪
 * @date 2023/11/14 16:39
 */
public interface UserService {
    public ResponseResult login(User user);

    ResponseResult logout();

    boolean register(String password, String userName);
}
