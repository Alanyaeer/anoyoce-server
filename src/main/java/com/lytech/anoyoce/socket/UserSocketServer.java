package com.lytech.anoyoce.socket;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lytech.anoyoce.service.UserService;
import com.lytech.anoyoce.utils.GetLoginUserUtils;
import com.lytech.anoyoce.utils.LivePersonUtils;
import com.lytech.anoyoce.utils.RoomUtils;
import com.lytech.anoyoce.utils.SpringCtxUtils;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/user/{userToken}")
@Component
@Slf4j
public class UserSocketServer {
//    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    @Resource
    private LivePersonUtils livePersonUtils;
    @Resource
    private WebSocketServer webSocketServer;
    @Resource
    private RoomUtils roomUtils;
    @OnOpen
        public void onOpen(Session session, @PathParam("userToken") String userToken) {
        Long userId = GetLoginUserUtils.getUserIdFromToken(userToken);
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        // 一个人员放入到这个 map 就是 在线的用户集合
        LivePersonUtils.inLivePerson(userId.toString(), session);
        // websocket注入失败问题
        log.info("有一连接打开，创建userid{}的用户session, 当前在线人数为：{}", userId, LivePersonUtils.getPersonInLiveNum());

        if(roomUtils == null){
            roomUtils = SpringCtxUtils.getBean(RoomUtils.class);
        }
        // 找到这个人加入的所有群 TODO
        List<Session> roomSessionList = roomUtils.queryConditionRoom(userToken);
        // 然后提醒这个所有这个群里的用户 后面在来写吧 ！
//        personOnlineAlert(roomSessionList);

    }

    /**
     * type来区分session的类型
     * type == 1的 时候 代表 接送消息
     * type == 2的 时候 客户端 维护一个 Map 来 判断 用户是否在线。
     * @param message
     * @param session
     * @param userToken
     */
    @OnMessage(maxMessageSize = 104048676)
    public void onMessage(String message, Session session, @PathParam("userToken") String userToken){
        // 一种 是 接受 消息 的 Message
        // 一种 是 接受 登录 消息的
        //设置 一个 工厂 来接受 这个 消息然后
        log.info("接收到消息");

        this.sendMessageAsync(message, session);
    }
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }
    @OnClose
    public void onClose(Session session, @PathParam("userToken") String userToken) {
        String userId = GetLoginUserUtils.getUserIdFromToken(userToken).toString();
        LivePersonUtils.outLivePerson(userId);
        log.info("有一连接关闭，移除userid{}的用户session, 当前在线人数为：{}", userId, LivePersonUtils.getPersonInLiveNum());
    }

    private void personOnlineAlert(List<Session> roomSessionList){
        if(roomSessionList == null || roomSessionList.size() == 1) return ;
        else {
            HashMap<String, Boolean> hasAlertPerson = new HashMap<>();
            // 每个群里面在线的人
            for(Session session : roomSessionList){
                // 从
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

    /**
     * 异步发送提高性能
     * @param message
     * @param toSession
     */
    private void sendMessageAsync(String message, Session toSession){
        if(toSession.isOpen()){
            toSession.getAsyncRemote().sendText(message);
        }
        else {
            log.info("这个session现在是关闭的");
        }
    }
}
