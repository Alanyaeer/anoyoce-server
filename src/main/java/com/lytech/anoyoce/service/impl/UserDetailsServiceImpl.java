package com.lytech.anoyoce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.lytech.anoyoce.domain.entity.LoginUser;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.mapper.MenuMapper;
import com.lytech.anoyoce.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author 三更  B站： https://space.bilibili.com/663528522
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    public UserMapper userMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName,username);
        User user = userMapper.selectOne(wrapper);
        //如果查询不到数据就通过抛出异常来给出提示
        if(Objects.isNull(user)){
            throw new RuntimeException("用户名或密码错误");
        }
//        List<String> list = new ArrayList<>(Arrays.asList("test", "admin"));
        List<String> list = menuMapper.selectPermsByUserId(user.getId());
        //封装成UserDetails对象返回 
        return new LoginUser(user, list);
    }
}
