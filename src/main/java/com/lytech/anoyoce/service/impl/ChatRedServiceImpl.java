package com.lytech.anoyoce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lytech.anoyoce.domain.entity.ChatRed;
import com.lytech.anoyoce.domain.vo.ChatRedVo;
import com.lytech.anoyoce.mapper.ChatRedMapper;
import com.lytech.anoyoce.service.ChatRedService;
import com.lytech.anoyoce.utils.GetLoginUserUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRedServiceImpl  extends ServiceImpl<ChatRedMapper, ChatRed> implements ChatRedService {
    @Override
    public List<ChatRed> queryByRoomId(String roomId) {
        LambdaQueryWrapper<ChatRed> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatRed::getRoomId, roomId);
        return this.list(queryWrapper);
    }

    @Override
    public int insertByRoomId(ChatRed chatRed) {
        return this.getBaseMapper().insert(chatRed);
    }
}
