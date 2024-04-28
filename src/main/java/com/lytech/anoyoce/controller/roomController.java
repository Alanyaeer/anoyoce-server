package com.lytech.anoyoce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.Room;
import com.lytech.anoyoce.domain.entity.UserRoom;
import com.lytech.anoyoce.mapper.RoomMapper;
import com.lytech.anoyoce.mapper.UserRoomMapper;
import com.lytech.anoyoce.service.RoomService;
import com.lytech.anoyoce.service.UserRoomService;
import com.lytech.anoyoce.utils.GetLoginUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.lytech.anoyoce.domain.enums.UserRoomType.ROOM_MEMBER;

@RestController
@RequestMapping("/room")
@Slf4j
public class roomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private UserRoomService userRoomService;

    /**
     * 添加群聊
     * @param roomId
     * @return
     */
    @PutMapping("/insert/room")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult insertRoom(@RequestParam("roomId") String roomId) {
        Room room = new Room();
        Long userId = GetLoginUserUtils.getUserId();
        room.setPid(userId);
        if(!StrUtil.isEmpty(roomId))
            room.setRootId(Long.valueOf(roomId));
        int insert = roomMapper.insert(room);
        return ResponseResult.success(insert);
    }

    /**
     * 加入某个群聊
     * @return
     */
    @PutMapping("/join/room")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult addRoom(@RequestParam("roomId") String roomId){
        // 查看群聊是否存在
        LambdaQueryWrapper<Room> roomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomLambdaQueryWrapper.eq(Room::getId, roomId);
        Integer count = roomMapper.selectCount(roomLambdaQueryWrapper);
        if(count.intValue() == 0){
            return ResponseResult.error(0);
        }
        // 加入到房间里面
        Integer joinOk =  userRoomService.joinUser(roomId);
        return new ResponseResult(200, "加入房间成功？", joinOk);
    }

    @DeleteMapping("/kick/user")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult kickUser(@RequestParam("roomId") String roomId, @RequestParam("memberId") String memberId){
        //首先 对 当前用户的身份进行一次校验
        Long userId = GetLoginUserUtils.getUserId();
        LambdaQueryWrapper<UserRoom> userRoomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoomLambdaQueryWrapper.eq(UserRoom::getUserId, userId);
        userRoomLambdaQueryWrapper.eq(UserRoom::getId, roomId);
        UserRoom userInfo = userRoomService.getOne(userRoomLambdaQueryWrapper);
        if(userInfo == null){
            return ResponseResult.error(0);
        }
        else {
            //等于普通用户
            if(userInfo.getUserType().intValue() == ROOM_MEMBER.getUserType()){
                // 用户的级别不够
                return ResponseResult.error(0);
            }
            else{
                boolean deleteOk =  userRoomService.removeUser(memberId);
                return ResponseResult.success(deleteOk);
            }
        }
    }
}
