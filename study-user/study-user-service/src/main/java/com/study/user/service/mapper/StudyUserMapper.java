package com.study.user.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.user.api.entity.StudyUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudyUserMapper extends BaseMapper<StudyUserEntity> {

    StudyUserEntity findByUsername(String username);

    StudyUserEntity findByWxOpenId(String openId);
}
