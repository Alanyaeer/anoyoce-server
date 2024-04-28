package com.lytech.anoyoce.controller;

import com.lytech.anoyoce.common.ResponseResult;
import com.lytech.anoyoce.domain.entity.ChatRed;
import com.lytech.anoyoce.service.ChatRedService;
import com.lytech.anoyoce.utils.GetLoginUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/chat")
@RestController
@Slf4j
public class ChatController {
    @Resource
    private ChatRedService chatRedService;
    @PostMapping("/query/chatInfoList")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult<List> queryChatInfoList(@RequestParam("roomId") String roomId) {
        List<ChatRed> chatRedList =  chatRedService.queryByRoomId(roomId);
        return ResponseResult.success(chatRedList);
    }

    @PutMapping("/insert/addChatInfo")
    @PreAuthorize("hasAuthority('vip')")
    public ResponseResult insertAddChatInfo(@RequestParam("roomId") String roomId, @RequestBody ChatRed chatRed) {
        Integer saveOk =   chatRedService.insertByRoomId(chatRed);
        return ResponseResult.success(saveOk);
    }

}
