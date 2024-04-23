package com.lytech.anoyoce.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 聊天记录的表格
 */
public class ChatRed {
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    @TableId
    /**
     * 主键
     */
    private Long id;
    /**
     * 房间的编号
     */
    private Long roomId;
    /**
     * 发送消息的人id
     */
    private Long pid;
    /**
     * 是否匿名发送
     */
    private Boolean anonymous;
    /**
     * 发送的消息（注意去限制长度）
     */
    private String message;
    /**
     * 消息的类型（0表示普通消息， 1表示卡片消息）
     */
    private Integer messageType;
    /**
     * 存入非0的数据的消息
     */
    private Object messageExtension;
}
