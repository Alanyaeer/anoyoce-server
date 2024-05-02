package com.lytech.anoyoce.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.Room;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.domain.entity.UserRoom;
import com.lytech.anoyoce.domain.vo.UserInfo;
import com.lytech.anoyoce.mapper.RoomMapper;
import com.lytech.anoyoce.mapper.UserRoomMapper;
import com.lytech.anoyoce.service.RoomService;
import com.lytech.anoyoce.service.UserRoomService;
import com.lytech.anoyoce.service.UserService;
import com.lytech.anoyoce.utils.GetLoginUserUtils;
import com.lytech.anoyoce.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.lytech.anoyoce.constants.RedisConstants.ROOM_ID;
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
    private UserService userService;
    @Autowired
    private UserRoomService userRoomService;
    @Autowired
    private RedisCache redisCache;

    /**
     * 创建群聊
     * @param roomName 房间的名称
     * @Param roomRootId 根房间id
     * @return
     */
    @PutMapping("/create/room")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult insertRoom(@RequestParam("roomName") String roomName, @RequestParam(value = "roomRootId", required = false) String roomRootId) {
        Room room = new Room();
        Long userId = GetLoginUserUtils.getUserId();
        room.setPid(userId);
        if(!StrUtil.isEmpty(roomName))
            room.setRoomName(roomName);
        else room.setRoomName("未名的聊天室");
        if(!StrUtil.isEmpty(roomRootId))
            room.setRootId(Long.valueOf(roomRootId));
        int insert = roomMapper.insert(room);
        //如果插入成功
        if(insert == 1){
            // 创建这个房间号的人加入到里面
            // ROOM_ID +":" + roomId 创建群聊
            userRoomService.joinUser(room.getId().toString());
            ArrayList<Long> userArrayList = new ArrayList<>();
            userArrayList.add(userId);
            redisCache.setCacheList(ROOM_ID + ":" + room.getId(), userArrayList);
        }
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
            return new ResponseResult(500, "加入房间失败， 不存在该房间", 0);
        }
        // 加入到房间里面
        Integer joinOk =  userRoomService.joinUser(roomId);
        if(joinOk.equals(1)){
            ArrayList<Long> userList = new ArrayList<>();
            Long userId = GetLoginUserUtils.getUserId();
            userList.add(userId);
            redisCache.setCacheList(ROOM_ID + ":" + roomId, userList);
        }
        return new ResponseResult(200, "", joinOk);
    }

    /**
     * 存在 一些逻辑问题， 比如说， 踢掉了自己之后， 怎么解决， 如果是群主怎么办， 我这里直接先禁止踢自己
     * @param roomId
     * @param memberId
     * @return
     */
    @DeleteMapping("/kick/user")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult kickUser(@RequestParam("roomId") String roomId, @RequestParam("memberId") String memberId){
        //首先 对 当前用户的身份进行一次校验
        Long userId = GetLoginUserUtils.getUserId();
        LambdaQueryWrapper<UserRoom> userRoomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoomLambdaQueryWrapper.eq(UserRoom::getUserId, userId);
        userRoomLambdaQueryWrapper.eq(UserRoom::getId, roomId);
        //查询房间号的信息 ， 方便进行后续的处理
        UserRoom userInfo = userRoomService.getOne(userRoomLambdaQueryWrapper);
        if(userInfo == null){
            return ResponseResult.error(0);
        }
        else {
            //等于普通用户 或者 等于自己
            if(userInfo.getUserType().intValue() == ROOM_MEMBER.getUserType() || (memberId.equals(userId.toString()))){
                // 用户的级别不够
                return ResponseResult.error(0);
            }
            else{
                boolean deleteOk =  userRoomService.removeUser(memberId, roomId);
                //从redis 里面 删除 这个 字段
                if(deleteOk){
                    // 从 redis 中 删除对应的字段
                    redisCache.delItemCacheList(ROOM_ID + ":" + roomId, memberId);
                }
                return ResponseResult.success(deleteOk);
            }
        }
    }
    @GetMapping("/query/user")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult queryRoomUser(@RequestParam(value = "roomId") String roomId){
        List<Long> cacheList = redisCache.getCacheList(ROOM_ID + ":" + roomId);
        if(!CollectionUtil.isEmpty(cacheList)){
            return ResponseResult.success(getTListToFList(cacheList));
        }
        LambdaQueryWrapper<UserRoom> wrapper = new LambdaQueryWrapper<>();
        if(!StrUtil.isEmpty(roomId))
            wrapper.eq(UserRoom::getId, roomId);
        List<UserRoom> userRoomList = userRoomService.list(wrapper);
        List<Long> userList = userRoomList.stream().map(e -> e.getId()).collect(Collectors.toList());
        //放入内存中
        redisCache.setCacheList(ROOM_ID + ":" + roomId, userList);

        return ResponseResult.success(getTListToFList(userList));
    }
    private  List<UserInfo> getTListToFList(Collection<Long> TList){
        Iterator<Long> iterator = TList.iterator();
        List<UserInfo> FList = new ArrayList<>();
        while(iterator.hasNext()){
            Long item = iterator.next();
            User user = userService.getById(item);
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(user, userInfo);
            FList.add(userInfo);
        }
        return FList;
    }
}