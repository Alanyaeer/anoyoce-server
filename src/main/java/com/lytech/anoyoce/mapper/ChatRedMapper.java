package com.lytech.anoyoce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lytech.anoyoce.domain.entity.ChatRed;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatRedMapper extends BaseMapper<ChatRed> {

    List<ChatRed> queryByRoomId(@Param("roomId") String roomId,@Param("userId") Long userId);
}
