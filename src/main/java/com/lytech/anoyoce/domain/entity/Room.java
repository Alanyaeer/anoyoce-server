package com.lytech.anoyoce.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 房间号
     */
    private Long id;
    /**
     * 创建房间的人的id
     */
    private Long pid ;
    /**
     * 父房间id
     */
    private Long rootId;
}
