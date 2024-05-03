package com.lytech.anoyoce;

import com.lytech.anoyoce.socket.RoomSocketServer;
import com.lytech.anoyoce.socket.UserSocketServer;
import com.lytech.anoyoce.socket.WebSocketServer;
import com.lytech.anoyoce.utils.SpringCtxUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
@MapperScan("com.lytech.anoyoce.mapper")
public class AnoyoceApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AnoyoceApplication.class);
        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);
        SpringCtxUtils springCtxUtils = new SpringCtxUtils();
        springCtxUtils.setApplicationContext(configurableApplicationContext);
    }

}
