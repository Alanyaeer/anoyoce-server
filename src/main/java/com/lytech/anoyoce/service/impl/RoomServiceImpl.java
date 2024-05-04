package com.lytech.anoyoce.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lytech.anoyoce.constants.AvatarConstants;
import com.lytech.anoyoce.domain.entity.Room;
import com.lytech.anoyoce.domain.entity.UserRoom;
import com.lytech.anoyoce.domain.vo.RoomVo;
import com.lytech.anoyoce.mapper.RoomMapper;
import com.lytech.anoyoce.service.RoomService;
import com.lytech.anoyoce.utils.GetLoginUserUtils;
import com.lytech.anoyoce.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.lytech.anoyoce.constants.RedisConstants.ROOM_INFO;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {
    @Autowired
    private RedisCache redisCache;

    @Override
    public RoomVo getRoomInfoById(String roomId) {
        // 先查询数据
        Map<String, Object> cacheMap = redisCache.getCacheMap(ROOM_INFO + ":" + roomId);
        if(CollectionUtil.isEmpty(cacheMap)){
            Room room = this.getById(roomId);
            Map<String, Object> beanToMap = BeanUtil.beanToMap(room);
            redisCache.setCacheMap(ROOM_INFO + ":" + roomId, beanToMap);
            cacheMap = beanToMap;
        }
        Room room = BeanUtil.toBean(cacheMap, Room.class);
        RoomVo roomVo = new RoomVo();
        BeanUtil.copyProperties(room, roomVo);
        if(StrUtil.isEmpty(room.getRoomAvatar())) roomVo.setRoomAvatar(AvatarConstants.avatar1);
        return roomVo;
    }
}
