package com.lytech.anoyoce.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {
    /**
     * 主键
     */
    @TableId
    public Long id;
    /**
     * 昵称
     */
    public String nickName;
    /**
     * 账号状态（0正常 1停用）
     */
    public String status;
    /**
     * 邮箱
     */
    public String email;
    /**
     * 手机号
     */
    public String phonenumber;
    /**
     * 用户性别（0男，1女，2未知）
     */
    public String sex;
    /**
     * 头像
     */
    public String avatar;
    /**
     * 用户类型（0管理员，1普通用户）
     */
    public String userType;
    /**
     * 创建时间
     */
    public LocalDateTime createTime;
    public String studentId;
    public UserInfo hiddenInfo(UserInfo userInfo){
        userInfo.setId(null);
        userInfo.setUserType("-1");
        userInfo.setStudentId("-1");
        userInfo.setSex("-1");
        userInfo.setCreateTime(LocalDateTime.MIN);
        userInfo.setEmail("-1");
        userInfo.setPhonenumber("-1");
        userInfo.setStatus("-1");
        return userInfo;
    }
}
