package com.lytech.anoyoce;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lytech.anoyoce.mapper")
public class AnoyoceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnoyoceApplication.class, args);
    }

}
