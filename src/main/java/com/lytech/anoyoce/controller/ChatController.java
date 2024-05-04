package com.lytech.anoyoce.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.ChatRed;
import com.lytech.anoyoce.domain.entity.User;
import com.lytech.anoyoce.domain.entity.UserRoom;
import com.lytech.anoyoce.domain.vo.ChatRedVo;
import com.lytech.anoyoce.domain.vo.UserInfo;
import com.lytech.anoyoce.service.ChatRedService;
import com.lytech.anoyoce.service.UserRoomService;
import com.lytech.anoyoce.service.UserService;
import com.lytech.anoyoce.utils.GetLoginUserUtils;
import com.lytech.anoyoce.utils.ItemToList;
import com.lytech.anoyoce.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lytech.anoyoce.constants.RedisConstants.USER_INFO_FULL;
import static com.lytech.anoyoce.constants.RedisConstants.USER_ROOM_CHAT;
import static com.lytech.anoyoce.domain.enums.MessageType.ORDINARY_MESSAGE;
import static com.lytech.anoyoce.domain.enums.MessageType.TASK_MESSAGE;

@RequestMapping("/chat")
@RestController
@Slf4j
public class ChatController {
    @Resource
    private ChatRedService chatRedService;
    @Resource
    private UserRoomService userRoomService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private UserService userService;
    @PostMapping("/query/chatInfoList")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult<List> queryChatInfoList(@RequestParam("roomId") String roomId) {
        List<ChatRed> cacheChatList = redisCache.getCacheList(USER_ROOM_CHAT + ":" + roomId);
        if(CollectionUtil.isEmpty(cacheChatList)){
            List<ChatRed> chatRedList =  chatRedService.queryByRoomId(roomId);
            // 消息的个数就是 0 这里直接就避免了 并且这里还避免了多次 放入消息到redis 的 问题
            if(CollectionUtil.isEmpty(chatRedList)){
                return ResponseResult.success(chatRedList);
            }
            redisCache.setCacheList(USER_ROOM_CHAT + ":" + roomId, chatRedList);
            cacheChatList = chatRedList;
        }
        // 组装
        List<ChatRedVo> chatRedVoList = cacheChatList.stream().map(e -> {
            ChatRedVo chatRedVo = new ChatRedVo();
            Long userId = e.getPid();
            Long myId = GetLoginUserUtils.getUserId();

            UserInfo userInfo = userService.getUserInfo(userId);
            // 匿名处理
            Integer anonymous = e.getAnonymous();
            BeanUtil.copyProperties(e, chatRedVo);
            // 判断是否是自己
            if(userId.equals(myId)){
                chatRedVo.setSelf(1);
            }
            else chatRedVo.setSelf(0);
            chatRedVo.setUserInfo(userInfo);
            if(anonymous == null || anonymous.intValue() == 0){
                userService.hiddenUserInfo(chatRedVo);
            }
            return chatRedVo;
        }).collect(Collectors.toList());
        return ResponseResult.success(chatRedVoList);
    }

    @PutMapping("/insert/addChatInfo")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult insertAddChatInfo(@RequestParam("roomId") String roomId, @RequestBody ChatRed chatRed) {
        Long userId = GetLoginUserUtils.getUserId();

        // 查看用户是否加入到这个房间里面 需要换成redis 来优化
        LambdaQueryWrapper<UserRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoom::getId, roomId);
        wrapper.eq(UserRoom::getUserId, userId);
        int count = userRoomService.count(wrapper);
        if(count == 0){
            return ResponseResult.error("该用户不在这个群聊之中");
        }
        chatRed.setPid(userId);
        chatRed.setRoomId(Long.valueOf(roomId));
        // ordinary 或者是 普通， 但是 这里 后续需要扩展， 所以还是得 正常的传入消息的类型
        if ((chatRed.getMessageType()) == null){
            if(chatRed.getMessageExtension() != null) chatRed.setMessageType(TASK_MESSAGE.getMessageType());
            else chatRed.setMessageType(ORDINARY_MESSAGE.getMessageType());
        }
        // 匿名处理 默认就是普通消息
        if(chatRed.getAnonymous() == null){
            chatRed.setAnonymous(0);
        }
        Integer saveOk =   chatRedService.insertByRoomId(chatRed);
        if(saveOk.intValue() == 1){
            redisCache.setCacheList(USER_ROOM_CHAT + ":" + roomId, ItemToList.getList(chatRed));
        }
        return ResponseResult.success(saveOk);
    }

}
