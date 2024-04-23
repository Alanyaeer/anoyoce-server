package com.lytech.anoyoce.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.lytech.anoyoce.constants.RedisConstants.ROOM_ID;

@Configuration
public class LivePersonUtils {
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 用户id - 用户session
     */
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 或者的同学就放入到里面
     * @param id
     * @param session
     */
    public static void inLivePerson(String id, Session session){
        if(sessionMap.containsKey(id) == false) sessionMap.put(id, session);
    }

    /**
     * 断开连接就拜拜
     * @param id
     */
    public static void outLivePerson(String id){
        sessionMap.remove(id);
    }

    /**
     * 查询所有符合房间号并且在线上的用户
     * @param id
     * @return
     */
    public Collection<Session> queryConditionPerson(String id){
        //先从redis 中获取到所有的用户列表
        List<String> userList = redisTemplate.opsForList().range(ROOM_ID, 0, -1);
        ArrayList<Session> sessionsValue = new ArrayList<>();
        for(String user: userList){
            // 如果 在线用户中存在 群里用户，那么就存入到数组里面。
            if(sessionMap.containsKey(user)){
                sessionsValue.add(sessionMap.get(user));
            }
        }
        return sessionsValue;
    }
}
