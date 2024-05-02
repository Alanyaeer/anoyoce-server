package com.lytech.anoyoce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lytech.anoyoce.domain.entity.UserRoom;

public interface UserRoomService extends IService<UserRoom> {
    Integer joinUser(String roomId);

    boolean removeUser(String memberId, String roomId);

}
