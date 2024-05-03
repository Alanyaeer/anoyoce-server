package com.lytech.anoyoce.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.domain.vo.ChatRedVo;
import com.lytech.anoyoce.domain.vo.UserInfo;

/**
 * @author 吴嘉豪
 * @date 2023/11/14 16:39
 */
public interface UserService extends IService<User> {
    public ResponseResult login(User user);

    ResponseResult logout();

    boolean register(String password, String userName);

    UserInfo getUserInfo(Long item);

    ChatRedVo hiddenUserInfo(ChatRedVo chatRedVo);
}
