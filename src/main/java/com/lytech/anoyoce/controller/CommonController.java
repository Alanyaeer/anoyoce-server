package com.lytech.anoyoce.controller;

import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.lytech.anoyoce.constants.RedisConstants.RANDOM_INFO_COUNT;
import static com.lytech.anoyoce.constants.RedisConstants.RANDOM_INFO_MSG;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Resource
    private RedisCache redisCache;
    @Autowired
    public RedisTemplate redisTemplate;
    @GetMapping("/importRandomInfo")
    public ResponseResult importInfo(@RequestParam("avatar") String avatar ,@RequestParam("nickName")  String nickName){
        Map<String, String> map = new HashMap<>();
        map.put("avatar", avatar);
        map.put("nickName", nickName);
        Integer currentCache = redisCache.getCacheObject(RANDOM_INFO_COUNT);
        if(currentCache == null) currentCache = 0;
        redisCache.setCacheMap(RANDOM_INFO_MSG +":" + currentCache, map);
        redisCache.increCacheObject(RANDOM_INFO_COUNT, 1L);
        return ResponseResult.success("添加成功");
    }
}
