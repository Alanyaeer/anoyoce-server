package com.lytech.anoyoce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lytech.anoyoce.domain.entity.Room;
import com.lytech.anoyoce.domain.entity.UserRoom;
import com.lytech.anoyoce.mapper.UserRoomMapper;
import com.lytech.anoyoce.service.RoomService;
import com.lytech.anoyoce.service.UserRoomService;
import com.lytech.anoyoce.utils.GetLoginUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.lytech.anoyoce.domain.enums.UserRoomType.ROOM_LEADER;
import static com.lytech.anoyoce.domain.enums.UserRoomType.ROOM_MEMBER;

@Service
public class UserRoomServiceImpl extends ServiceImpl<UserRoomMapper, UserRoom> implements UserRoomService {
    @Autowired
    private RoomService roomService;
    @Override
    public Integer joinUser(String roomId) {
        UserRoom userRoom = new UserRoom();
        Long userId = GetLoginUserUtils.getUserId();
        userRoom.setUserId(userId);
        //房间号的id
        userRoom.setId(Long.valueOf(roomId));
        //查询房间号的主人， 然后判断是不是群主
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Room::getPid);
        wrapper.eq(Room::getId, roomId);
        Room roomItem = roomService.getOne(wrapper);
        if(roomItem.getPid().equals(userId))
            userRoom.setUserType(ROOM_LEADER.getUserType());
        else
        userRoom.setUserType(ROOM_MEMBER.getUserType());

        boolean save = save(userRoom);
        if(save) return 1;
        else return 0;
    }

    @Override
    public boolean removeUser(String memberId, String roomId) {
        //根据 用户 的 id 移除
        LambdaQueryWrapper<UserRoom> userRoomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoomLambdaQueryWrapper.eq(UserRoom::getUserId, memberId);
        userRoomLambdaQueryWrapper.eq(UserRoom::getId, roomId);
        return remove(userRoomLambdaQueryWrapper);

    }

}
