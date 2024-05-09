package com.lytech.anoyoce.utils;

import com.lytech.anoyoce.domain.entity.LoginUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class UserPrincipalUtils {
    public static String  getUserId(UsernamePasswordAuthenticationToken userPrinciPal){
        LoginUser principal = (LoginUser)userPrinciPal.getPrincipal();
        return principal.getUser().getId().toString();
    }
}
