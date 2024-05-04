package com.lytech.anoyoce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lytech.anoyoce.domain.entity.Room;
import com.lytech.anoyoce.domain.vo.RoomVo;

import java.util.List;

public interface RoomService extends IService<Room> {
    RoomVo getRoomInfoById(String roomId);
}
