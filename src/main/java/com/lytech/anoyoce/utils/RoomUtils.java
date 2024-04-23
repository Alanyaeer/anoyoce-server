package com.lytech.anoyoce.utils;

import com.lytech.anoyoce.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lytech.anoyoce.constants.RedisConstants.USER_IN_ROOM;

@Configuration
@Slf4j
public class RoomUtils {
    // 房间号的 Session
    private static final  Map<String, Session> roomMap =  new ConcurrentHashMap<>();
    @Autowired
    private StringRedisTemplate redisTemplate;
    @PostConstruct
    void loadRoomInfo(){
        // 从 redis 里面 读取到这里
    }

    // 根据用户的id 来 获取到这个用户加入到的房间号的所有Sessions
    public List<Session>  queryConditionRoom (){
        Long userId = GetLoginUserUtils.getUserId();
        ArrayList<Session> arrayList = new ArrayList<>();
        // 这个 用户他加入到的群聊
        List<String> UserInRoom = redisTemplate.opsForList().range(USER_IN_ROOM +":" +userId.toString() , 0, -1);
        List<Session> sessionList = new ArrayList<>();
        for(String room: UserInRoom){
            if(roomMap.containsKey(room)) sessionList.add(roomMap.get(room));
        }
        return sessionList;
    }

}
