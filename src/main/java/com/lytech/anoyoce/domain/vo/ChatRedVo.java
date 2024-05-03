package com.lytech.anoyoce.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRedVo implements Serializable {
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 是否匿名发送
     */
    private Integer anonymous;
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
    /**
     * 用户信息
     */
    private UserInfo userInfo;
    /**
     * 是否是本人 0代表是别人， 1代表是自己
     */
    private Integer self;
}
