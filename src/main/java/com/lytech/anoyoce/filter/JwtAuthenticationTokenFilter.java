package com.lytech.anoyoce.filter;


import cn.hutool.core.util.StrUtil;
import com.lytech.anoyoce.domain.entity.LoginUser;
import com.lytech.anoyoce.utils.JwtUtil;
import com.lytech.anoyoce.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.lytech.anoyoce.constants.RedisConstants.LOGIN_USER_INFO;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("token");
        if(StrUtil.isEmpty(token)){
            // 解决websocket token 的 问题
            token = request.getHeader("Sec-Websocket-Protocol");
            // 给响应加上 socket 头
            response.setHeader("Sec-Websocket-Protocol", token);
        }
        if (!StringUtils.hasText(token)) {
            //放行, 没有token就让后面去放行
            filterChain.doFilter(request, response);
            return;
        }
        //解析token
        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        //从redis中获取用户信息 这里不考虑过期的问题
        String redisKey = LOGIN_USER_INFO +": " + userid;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if(Objects.isNull(loginUser)){
            throw new RuntimeException("用户未登录");
        }
        //存入SecurityContextHolder， 需要使用 将LoginUser 改成 UsernamePasswordAuthenticationToken
        //TODO 获取权限信息封装到Authentication中
        // 从这里传入权限信息

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //放行
        filterChain.doFilter(request, response);
    }
}
