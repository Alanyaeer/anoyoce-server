package com.lytech.anoyoce.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.LoginUser;
import com.lytech.anoyoce.domain.entity.SysUserRole;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.mapper.SysUserRoleMapper;
import com.lytech.anoyoce.mapper.UserMapper;
import com.lytech.anoyoce.service.UserService;
import com.lytech.anoyoce.utils.JwtUtil;
import com.lytech.anoyoce.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author 吴嘉豪
 * @date 2023/11/14 16:39
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 判断上面这个是不是为null， 如果为null 说明没有通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("登录失败");
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userid = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userid);

        // 认证通过
        // authenticate.getPrincipal()
        // 把完整用户信息存入到redis 中
        HashMap<String, String> map = new HashMap<>();
        map.put("token", jwt);
        redisCache.setCacheObject("login: " + userid , loginUser);
        return new ResponseResult(200, "登录成功", jwt);
    }

    @Override
    public ResponseResult logout() {
        // 获取securityContextHolder 中的用户id
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        Long userid =loginUser.getUser().getId();
        redisCache.deleteObject("login: "+userid);
        // 删除redis当中的值
        return new ResponseResult(200, "注销成功");
    }

    @Override
    public boolean register(String password, String userName) {
        return registerType(userName, password, 2L);
    }
    private boolean registerType(String userName, String password, Long userType) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, userName);
        User user = userMapper.selectOne(wrapper);
        if(Objects.isNull(user) == false){
            return false;
        }
        User userNew = new User();
        // 使用 BCryptPasswordEncoder进行判断
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(password);

        userNew.setPassword(encode);
        userNew.setUserName(userName);
        userNew.setAvatar("https://picsum.photos/60/60");
        userNew.setNickName("momo");

        userMapper.insert(userNew);
        // 重新获取到用户的id， 然后
        User userone = userMapper.selectOne(wrapper);
        Long id = userone.getId();
        // 存入到sysuserroleMapper里面
        sysUserRoleMapper.insert(new SysUserRole(id, userType));


        return true;
    }

}
