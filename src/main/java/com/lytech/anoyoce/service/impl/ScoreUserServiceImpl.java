package com.lytech.anoyoce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lytech.anoyoce.domain.entity.ScoreUser;
import com.lytech.anoyoce.mapper.ScoreUserMapper;
import com.lytech.anoyoce.service.ScoreUserService;
import org.springframework.stereotype.Service;

@Service
public class ScoreUserServiceImpl extends ServiceImpl<ScoreUserMapper, ScoreUser> implements ScoreUserService {

    @Override
    public ScoreUser getOneByCondition(String roomId, Long myId, String userId, Long times) {
        LambdaQueryWrapper<ScoreUser> scoreUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scoreUserLambdaQueryWrapper.eq(ScoreUser::getRoomId, roomId);
        scoreUserLambdaQueryWrapper.eq(ScoreUser::getPid, myId  );
        scoreUserLambdaQueryWrapper.eq(ScoreUser::getUserId, userId);
        scoreUserLambdaQueryWrapper.eq(ScoreUser::getTimes, times);
        ScoreUser one = this.getOne(scoreUserLambdaQueryWrapper);
        return one;
    }
}
