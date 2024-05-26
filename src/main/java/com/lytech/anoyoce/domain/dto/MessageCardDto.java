package com.lytech.anoyoce.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 它是ChatRed 的 一个 extends 类型
 */
public class MessageCardDto implements Serializable {
    /**
     * 主题内容
     */
    private String subject;
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    /**
     * 卡片内容
     */
    private String content;
    /**
     * 选择的用户ID
     */
    private String choseFriend;
    /**
     * 查询的次数
     */
    private Long times;
}
