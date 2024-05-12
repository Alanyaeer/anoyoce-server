package com.lytech.anoyoce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lytech.anoyoce.domain.entity.ScoreUser;

public interface ScoreUserService extends IService<ScoreUser> {
    ScoreUser getOneByCondition(String roomId, Long myId, String userId);
}
