package com.lytech.anoyoce.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.constants.AvatarConstants;
import com.lytech.anoyoce.domain.entity.Room;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.domain.entity.UserRoom;
import com.lytech.anoyoce.domain.vo.RoomVo;
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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lytech.anoyoce.constants.RedisConstants.*;
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
            // 房间信息转变 为 map 然后存入到 redis 里面
            Map<String, Object> objectMap = BeanUtil.beanToMap(room);

            redisCache.setCacheMap(ROOM_INFO + ":" + room.getId(), objectMap);
            // 设置过期时间
            redisCache.expire(ROOM_INFO + ":" + room.getId(), 60, TimeUnit.MINUTES);
            return new ResponseResult(200, "插入成功", room);

        }
        return ResponseResult.error("插入房间失败");
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
            // 这个用户加入到的房间号
            HashSet<Long> userListInRoom = new HashSet<>();
            userListInRoom.add(Long.valueOf(roomId));

            redisCache.setCacheSet(USER_ADD_ROOM + ":" + userId, userListInRoom);

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
                    // 这个 roomSet 是 要被删除的房间号
                    Set<Long> roomSet = new HashSet<>();
                    roomSet.add(Long.valueOf(roomId));
                    redisCache.revCacheSet(USER_ADD_ROOM + ":" + userId, roomSet);
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
            UserInfo userInfo =  userService.getUserInfo(item);
            FList.add(userInfo);
        }
        return FList;
    }

    /**
     * 查询某个用户 加入了 哪些房间 已经全部上缓存
     * @param userId
     * @return
     */
    @GetMapping("/query/roomAdd")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult queryRoomUserAdd(@RequestParam(value = "userId", required = false) String userId){
        if(StrUtil.isEmpty(userId)) userId = String.valueOf(GetLoginUserUtils.getUserId());
        Set<Object> cacheSet = redisCache.getCacheSet(USER_ADD_ROOM + ":" + userId);
        List<Object> roomList = cacheSet.stream().collect(Collectors.toList());
        if(CollectionUtil.isEmpty(roomList)){
            List<UserRoom> userRoomList =  userRoomService.queryRoomUserIn();
            Set<Long> roomSet = new HashSet<>();
            List<RoomVo> roomVos = userRoomList.stream().map(e -> {
                RoomVo roomVo = new RoomVo();
                BeanUtil.copyProperties(e, roomVo);
                Long roomId = e.getId();
                roomSet.add(roomId);
                roomVo.setRoomAvatar(AvatarConstants.avatar1);
                return roomVo;
            }).collect(Collectors.toList());
            // 添加进来
            redisCache.setCacheSet(USER_ADD_ROOM + ":" + userId, roomSet);
            return ResponseResult.success(roomVos);
        }
        List<RoomVo> roomVos = roomList.stream().map(e -> {
            RoomVo roomVo = new RoomVo();
            Map<String, Object> cacheMap = redisCache.getCacheMap(ROOM_INFO + ":" + e);
            if (CollectionUtil.isEmpty(cacheMap)) {
                Room roomInfo = roomService.getById(((Long)e));
                // 把 roomInfo  赋值 给 roomVo
                BeanUtil.copyProperties(roomInfo, roomVo);
                Map<String, Object> roomInfoMap = BeanUtil.beanToMap(roomInfo);
                redisCache.setCacheMap(ROOM_INFO + ":" + e, roomInfoMap);
            }
            Room roomVoSed = BeanUtil.toBean(cacheMap, Room.class);
            BeanUtil.copyProperties(roomVoSed, roomVo);
            roomVo.setRoomAvatar(AvatarConstants.avatar1);
            return roomVo;
        }).collect(Collectors.toList());
        //  后续还要做处理
        return ResponseResult.success(roomVos);
    }

    /**
     * 通过查询 房间号的id 来 获取到 房间的信息
     * @param roomId
     * @return
     */
    @GetMapping("/query/roomId")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult queryRoomInfoById(@RequestParam(value = "roomId", required = false) String roomId){
        RoomVo roomVo =  roomService.getRoomInfoById(roomId);
        return ResponseResult.success(roomVo);
    }
}
