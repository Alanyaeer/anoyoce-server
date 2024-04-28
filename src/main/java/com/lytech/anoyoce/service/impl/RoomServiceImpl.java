package com.lytech.anoyoce.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lytech.anoyoce.domain.entity.Room;
import com.lytech.anoyoce.mapper.RoomMapper;
import com.lytech.anoyoce.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {
}
