package com.lytech.anoyoce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lytech.anoyoce.domain.entity.ChatRed;

import java.util.List;

public interface ChatRedService extends IService<ChatRed> {

    List<ChatRed> queryByRoomId(String roomId);

    int insertByRoomId(ChatRed chatRed);
}
