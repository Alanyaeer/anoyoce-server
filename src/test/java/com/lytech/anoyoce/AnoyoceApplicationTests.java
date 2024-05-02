package com.lytech.anoyoce;

import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class AnoyoceApplicationTests {
    @Autowired
    private UserMapper userMapper;
    @Test
    void contextLoads() {
    }
    @Test
    void testUserMapper(){
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
    }

}
