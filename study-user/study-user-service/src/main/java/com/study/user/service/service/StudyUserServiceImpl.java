package com.study.user.service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.study.common.core.utils.BeanUtil;
import com.study.common.core.utils.PublicUtil;
import com.study.user.api.entity.StudyUserEntity;
import com.study.user.api.response.StudyUserResponse;
import com.study.user.api.service.StudyUserService;
import com.study.user.service.mapper.StudyUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StudyUserServiceImpl implements StudyUserService {

    @Autowired
    private StudyUserMapper studyUserMapper;


    @Override
    public StudyUserResponse getModelByLoginId(String studyUserId) {
        if (StringUtils.isBlank(studyUserId)) {
            return null;
        }
        QueryWrapper<StudyUserEntity> queryWrapper =
                new QueryWrapper<StudyUserEntity>().eq("user_id", studyUserId);

        StudyUserEntity studyUserEntity = studyUserMapper.selectOne(queryWrapper);
        if (PublicUtil.isEmpty(studyUserEntity)) {
            return null;
        }

        return convertToResponse(studyUserEntity);
    }

    private StudyUserResponse convertToResponse(StudyUserEntity studyUserEntity) {
        StudyUserResponse response = BeanUtil.shallowCopy(studyUserEntity, StudyUserResponse.class);
        response.setStudyUserId(String.valueOf(studyUserEntity.getId()));
        return response;
    }
}
