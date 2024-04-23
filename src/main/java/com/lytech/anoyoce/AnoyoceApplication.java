package com.lytech.anoyoce;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@Slf4j
@SpringBootApplication
@MapperScan("com.lytech.anoyoce.mapper")
public class AnoyoceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnoyoceApplication.class, args);
    }

}
