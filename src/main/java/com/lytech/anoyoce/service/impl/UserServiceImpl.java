package com.lytech.anoyoce.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.LoginUser;
import com.lytech.anoyoce.domain.entity.SysUserRole;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.domain.vo.ChatRedVo;
import com.lytech.anoyoce.domain.vo.UserInfo;
import com.lytech.anoyoce.mapper.SysUserRoleMapper;
import com.lytech.anoyoce.mapper.UserMapper;
import com.lytech.anoyoce.service.UserService;
import com.lytech.anoyoce.utils.GenerateRandomUtils;
import com.lytech.anoyoce.utils.JwtUtil;
import com.lytech.anoyoce.utils.LivePersonUtils;
import com.lytech.anoyoce.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.lytech.anoyoce.constants.RedisConstants.*;

/**
 * @author 吴嘉豪
 * @date 2023/11/14 16:39
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
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

    /**
     * 给一个用户的id 来获取到UserInfo
     * @param userId
     * @return
     */
    @Override
    public UserInfo getUserInfo(Long userId) {
        /**
         * 从 缓存里面 查找
         */
        Map<String, Object> cacheMap = redisCache.getCacheMap(USER_INFO_FULL + ":" + userId);
        // 如果是空的或者是没有的话
        UserInfo userInfo = new UserInfo();

        if(CollectionUtil.isEmpty(cacheMap)){
            User user = this.getById(userId);
            BeanUtils.copyProperties(user, userInfo);
            //添加缓存
            Map<String, Object> objectMap = BeanUtil.beanToMap(user);
            redisCache.setCacheMap(USER_INFO_FULL + ":" + userId, objectMap);
            redisCache.expire(USER_INFO_FULL+ ":" + userId, 5, TimeUnit.MINUTES);
        }
        else{
            User user = BeanUtil.toBean(cacheMap, User.class);
            BeanUtils.copyProperties(user, userInfo);
        }
        // 在线处理
        userInfo.setOnline(LivePersonUtils.judgePersonOnline(userInfo.getId()));
        return userInfo;
    }

    @Override
    public ChatRedVo hiddenUserInfo(ChatRedVo chatRedVo) {
        Integer anonymous = chatRedVo.getAnonymous();
        // 需要匿名
        if(anonymous == null || anonymous.intValue() == 0){
            UserInfo userInfo = chatRedVo.getUserInfo();
            // 主键隐藏
            userInfo.hiddenInfo(userInfo);
            // 随机获取 一个头像或者名称
//            RANDOM_INFO
            Integer cacheMaxNumber = redisCache.getCacheObject(RANDOM_INFO_COUNT);
            int randomNumber = 0;
            if(cacheMaxNumber != null){
                randomNumber = GenerateRandomUtils.randomNumber(cacheMaxNumber);
                Map<String, Object> cacheMap = redisCache.getCacheMap(RANDOM_INFO_MSG + ":" + randomNumber);
                userInfo.setNickName((String) cacheMap.get("nickName"));
                userInfo.setAvatar((String) cacheMap.get("avatar"));
            }
            else {
                //TODO 这里 还需要 在 对头像进行完善
                userInfo.setNickName("momo");
                userInfo.setAvatar("-1");
            }
        }
        return chatRedVo;
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
