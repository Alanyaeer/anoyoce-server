package com.lytech.anoyoce.socket;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lytech.anoyoce.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ServerEndpoint(value = "/room/{roomId}")
@Component
public class RoomSocketServer {
    @Autowired
    private LivePersonUtils personUtils;
    @Autowired
    private RoomUtils roomUtils;
    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId){
        if(roomUtils == null) {
            roomUtils = SpringCtxUtils.getBean(RoomUtils.class);
        }
        String userId = UserPrincipalUtils.getUserId((UsernamePasswordAuthenticationToken) session.getUserPrincipal());
        roomUtils.viewInRoom(userId, roomId, session);
        roomUtils.LogViewInRoom(userId, roomId, "login");
        log.info("房间已经打开");
    }
    @OnClose
    public void onClose(Session session,  @PathParam("roomId") String roomId) {
        String userId = UserPrincipalUtils.getUserId((UsernamePasswordAuthenticationToken) session.getUserPrincipal());
        if(roomUtils == null) roomUtils = SpringCtxUtils.getBean(RoomUtils.class);
        roomUtils.viewOutRoom(userId, roomId, session);
        roomUtils.LogViewInRoom(userId, roomId, "logout");
        log.info("有一连接关闭");
    }
    @OnMessage(maxMessageSize = 104048676)
    public void onMessage(String message, Session session, @PathParam("roomId") String roomId){
        // 我们维护一个Map 判断是否给某个用户是否发送过登录的 消息
        JSONObject jsonObject = JSONUtil.parseObj(message);
        String type = jsonObject.getStr("type");
        String token = jsonObject.getStr("token");

//         jsonObject.get("message");
        // 发送消息
        // type == login

        if(type.equals("chat")){
            // websocket 注入失败处理
            if(personUtils == null){
                personUtils =  SpringCtxUtils.getBean(LivePersonUtils.class);
            }
            List<Session> sessionPersonList = personUtils.queryConditionPerson(roomId); // 查询这个群里所有的人

            for(Session sessionItem: sessionPersonList){
                JSONObject pacMsg = JSONUtil.parseObj(message);
                int isSelf = 0;
                if(sessionItem.getPathParameters().get("userToken").equals(token)) isSelf = 1;
                ((JSONObject)pacMsg.get("message")).set("self", isSelf);

                sendMessage(pacMsg.toString(), sessionItem);
            }
        }
        // 通知所在群的人 ， 自己当前上线了这里
        else if(type.equals("login")){

        }

    }
    private void sendMessage(String message, Session toSession) {
        try {
            // TODO 可能会有错误
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }
    /**
     * 异步发送提高性能
     * @param message
     * @param toSession
     */
    private void sendMessageAsync(String message, Session toSession){
        if(toSession.isOpen()){
            // 处理self 和 time的问题

            toSession.getAsyncRemote().sendText(message);
        }
        else {
            log.info("这个session现在是关闭的");
        }
    }
}
