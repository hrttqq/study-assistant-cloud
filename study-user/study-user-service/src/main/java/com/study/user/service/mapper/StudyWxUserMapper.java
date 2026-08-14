package com.study.user.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.user.api.entity.StudyWxUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudyWxUserMapper extends BaseMapper<StudyWxUserEntity> {
    StudyWxUserEntity selectByOpenId(String openId, String wxUserStatus);


}
