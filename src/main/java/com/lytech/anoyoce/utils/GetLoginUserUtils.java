package com.lytech.anoyoce.utils;


import com.lytech.anoyoce.domain.entity.LoginUser;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author 吴嘉豪
 * @date 2024/1/5 16:56
 */
public class GetLoginUserUtils {
    public static LoginUser getLoginUser(){
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        return loginUser;
    }
    public static Long getUserId(){
        return getLoginUser().getUser().getId();
    }
    public static Long getUserIdFromToken(String token){
        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Long.valueOf(userId);

    }

    public static boolean isManager() {
        return getLoginUser().getPermissions().contains("manager");
    }
    public static boolean isPmk(){
        return getLoginUser().getPermissions().contains("pmk");
    }
}
