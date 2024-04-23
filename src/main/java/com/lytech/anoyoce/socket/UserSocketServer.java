package com.lytech.anoyoce.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/imserver/{userId}")
@Component
public class UserSocketServer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);


}
