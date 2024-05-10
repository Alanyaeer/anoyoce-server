package com.lytech.anoyoce.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.domain.entity.UserRoom;
import com.lytech.anoyoce.service.UserRoomService;
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
import java.util.stream.Collectors;

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
    @Resource
    private UserRoomService userRoomService;

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
    // 这里的session 没有作用
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
    public List<Long> queryUserAddRoom(String userId){
        Set<Object> cacheSet = redisCache.getCacheSet(USER_ADD_ROOM + ":" + userId);
        List<Long> roomList = cacheSet.stream().map(e-> (Long) e).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(roomList)){
            // 从数据库拿取
            List<UserRoom> userRoomList =  userRoomService.queryRoomUserInById(userId);
            Set<Long> set = new HashSet<>();
            List<Long> list = userRoomList.stream().map(e -> {
                set.add(e.getId());
                return e.getId();
            }).collect(Collectors.toList());
            redisCache.setCacheSet(USER_ADD_ROOM + ":" + userId, set);
            return list;
        }
        return roomList;
    }
    public void LogViewInRoom(String userId, String roomId, String type) {
        // 先根据 用户id 来找到所有用户的 redis 缓存
        List<Long> roomList = this.queryUserAddRoom(userId);
        // 遍历每一个房间通知每一个人
        for(int i = 0; i < roomList.size(); ++i){
            roomId = roomList.get(i).toString();
            Map<String, Session> map = viewRoom.get(roomId);
            if(CollectionUtil.isEmpty(map)) continue;
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
}
