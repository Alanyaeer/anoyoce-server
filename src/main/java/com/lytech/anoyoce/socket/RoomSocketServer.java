package com.lytech.anoyoce.socket;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lytech.anoyoce.utils.LivePersonUtils;
import com.lytech.anoyoce.utils.RoomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@ServerEndpoint(value = "/room/{roomId}")
public class RoomSocketServer {
    @Autowired
    private LivePersonUtils personUtils;
    @Autowired
    private RoomUtils roomUtils;
    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String room){
        log.info("房间已经打开");
    }

    @OnMessage(maxMessageSize = 104048676)
    public void onMessage(String message, Session session, @PathParam("roomId") String roomId){
        // 我们维护一个Map 判断是否给某个用户是否发送过登录的 消息
        JSONObject jsonObject = JSONUtil.parseObj(message);
        String type = jsonObject.getStr("type");
        // 这里似乎 可以用到工厂模式
        if(type.equals("1")){
            List<Session> sessionPersonList = personUtils.queryConditionPerson(roomId); // 查询这个群里所有的人
            for(Session sessionItem: sessionPersonList)
                sendMessage(message, sessionItem);
        }
        else if(type.equals("2")){
            //登录的发送请求
            List<Session> sessionGroupList = roomUtils.queryConditionRoom(); // 这个发送的用户他加入的所有群聊
            //定义一个HashMap，如果 某个用户 直接已经在Map里面了，就不用在发送这登录消息了
            Map<String, Integer> hashMap = new HashMap<>();
            for(Session sessionItem: sessionGroupList){
                // getId 返回的 是 连接的人员 序号 （默认从1开始升序）， 所以这里可能 有一丝丝问题， 但是不要紧
                if(!hashMap.containsKey(sessionItem.getId())) {
                    this.sendMessage(message, sessionItem);
                    hashMap.put(session.getId(), 1);
                }
            }
        }
    }
    private void sendMessage(String message, Session toSession) {
        try {
            // TODO 可能会有错误
//            toSession.get
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }
}
