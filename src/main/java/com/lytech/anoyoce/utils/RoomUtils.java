package com.lytech.anoyoce.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lytech.anoyoce.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.lytech.anoyoce.constants.RedisConstants.USER_ADD_ROOM;

@Component
@Slf4j
public class RoomUtils {
    // 房间号的 Session
    private static final Map<String, Session> roomMap =  new ConcurrentHashMap<>();
    private static final Map<String, Map<String, Session>> viewRoom = new ConcurrentHashMap<>();
    @Resource
    private RedisCache redisCache;
    @Resource
    private LivePersonUtils livePersonUtils;

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
    public void viewInRoom(String userId, String roomId, Session session){
        if(viewRoom.containsKey(roomId)){
            Map<String, Session> userRoomSessionMap =  viewRoom.get(roomId);
            userRoomSessionMap.put(userId, session);
        }
        else {
            ConcurrentHashMap<String, Session> userSessionMap = new ConcurrentHashMap<>();
            userSessionMap.put(userId, session);
            viewRoom.put(roomId, userSessionMap);
        }
    }
    public void viewOutRoom(String userId, String roomId, Session session){
        if(viewRoom.containsKey(roomId)){
            Map<String, Session> userRoomSessionMap =  viewRoom.get(roomId);
            if(userRoomSessionMap.containsKey(userId))
            userRoomSessionMap.remove(userId);
        }
    }

    public void LogViewInRoom(String userId, String roomId, String type) {
        Map<String, Session> map = viewRoom.get(roomId);
        Set<String> userIdSet = map.keySet();
        Iterator<String> iterator = userIdSet.iterator();
        while (iterator.hasNext()){
            String userSessionId = iterator.next();
            Session userSession = livePersonUtils.getUserSession(userSessionId);
            JSONObject info = new JSONObject();
            info.set("type", type);
            info.set("userId", userId);
            userSession.getAsyncRemote().sendText(JSONUtil.toJsonStr(info));
        }
    }
}
