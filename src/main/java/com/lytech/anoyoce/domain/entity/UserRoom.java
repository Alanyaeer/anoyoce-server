package com.lytech.anoyoce.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 *  房间号的用户 - 这个可以上一个redis来存储。
 */
public class UserRoom {
    /**
     * chatRed外键
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 改用户是否为管理员（0表示普通人员，1表示管理员，2表示群主）
     */
    private Integer userType;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
