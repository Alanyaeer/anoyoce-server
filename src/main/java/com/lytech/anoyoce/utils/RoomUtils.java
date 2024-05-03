package com.lytech.anoyoce.utils;

import cn.hutool.core.bean.BeanUtil;
import com.lytech.anoyoce.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lytech.anoyoce.constants.RedisConstants.USER_ADD_ROOM;
import static com.lytech.anoyoce.constants.RedisConstants.USER_IN_ROOM;

@Component
@Slf4j
public class RoomUtils {
    // 房间号的 Session
    private static final Map<String, Session> roomMap =  new ConcurrentHashMap<>();
    @Resource
    private RedisCache redisCache;

    // 根据用户的id 来 获取到这个用户加入到的房间号的所有Sessions

    public  List<Session>  queryConditionRoom (String userToken){
        Long userId = GetLoginUserUtils.getUserIdFromToken(userToken);
        ArrayList<Session> arrayList = new ArrayList<>();
        // 这个用户他加入到的群聊
//        List<String> UserInRoom = redisTemplate.opsForList().range(USER_IN_ROOM +":" +userId.toString() , 0, -1);
        List<String> UserInRoom = BeanUtil.toBean(redisCache.getCacheSet(USER_ADD_ROOM + ":" + userId), List.class);
        List<Session> sessionList = new ArrayList<>();
        for(String room: UserInRoom){
            if(roomMap.containsKey(room)) sessionList.add(roomMap.get(room));
        }
        return sessionList;
    }

}
