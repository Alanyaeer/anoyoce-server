package com.lytech.anoyoce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lytech.anoyoce.domain.entity.UserRoom;
import com.lytech.anoyoce.mapper.UserRoomMapper;
import com.lytech.anoyoce.service.UserRoomService;
import com.lytech.anoyoce.utils.GetLoginUserUtils;
import org.springframework.stereotype.Service;

import static com.lytech.anoyoce.domain.enums.UserRoomType.ROOM_MEMBER;

@Service
public class UserRoomServiceImpl extends ServiceImpl<UserRoomMapper, UserRoom> implements UserRoomService {
    @Override
    public Integer joinUser(String roomId) {
        UserRoom userRoom = new UserRoom();
        Long userId = GetLoginUserUtils.getUserId();
        userRoom.setUserId(userId);
        //房间号的id
        userRoom.setId(Long.valueOf(roomId));
        userRoom.setUserType(ROOM_MEMBER.getUserType());
        boolean save = save(userRoom);
        if(save) return 1;
        else return 0;
    }

    @Override
    public boolean removeUser(String memberId) {
        //根据 用户 的 id 移除
        LambdaQueryWrapper<UserRoom> userRoomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoomLambdaQueryWrapper.eq(UserRoom::getUserId, memberId);
        return remove(userRoomLambdaQueryWrapper);

    }
}
