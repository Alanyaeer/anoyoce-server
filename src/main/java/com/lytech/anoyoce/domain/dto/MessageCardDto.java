package com.lytech.anoyoce.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 它是ChatRed 的 一个 extends 类型
 */
public class MessageCardDto {
    /**
     * 主题内容
     */
    private String subject;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 卡片内容
     */
    private String content;

}
