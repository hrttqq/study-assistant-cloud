package com.study.user.service.service;

import com.study.common.core.enumConstants.DicEnum;
import com.study.common.core.utils.BeanUtil;
import com.study.user.api.dto.StudyWxUserRegisterDTO;
import com.study.user.api.entity.StudyUserEntity;
import com.study.user.api.entity.StudyWxUserEntity;
import com.study.user.api.service.StudyWxUserService;
import com.study.user.api.response.StudyWxUserResponse;
import com.study.user.service.mapper.StudyUserMapper;
import com.study.user.service.mapper.StudyWxUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class StudyWxUserServiceImpl implements StudyWxUserService {

    @Autowired
    private StudyWxUserMapper studyWxUserMapper;

    @Autowired
    private StudyUserMapper userMapper;


    /**
     * 根据openId查询微信用户数据
     *
     * @param openId
     * @param wxUserStatus
     * @return
     */
    @Override
    public StudyWxUserResponse getOpenId(String openId, String wxUserStatus) {
        if (StringUtils.isBlank(openId)) {
            return null;
        }

        StudyWxUserEntity entity = studyWxUserMapper.selectByOpenId(openId, wxUserStatus);
        if (entity == null) {
            return null;
        }

        return convertToVO(entity);
    }

    /**
     * 创建微信用户
     *
     * @param wxUserRegisterDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudyWxUserResponse createStudyWxUser(StudyWxUserRegisterDTO wxUserRegisterDTO) {
        StudyWxUserEntity oldWxUser = studyWxUserMapper.selectByOpenId(wxUserRegisterDTO.getOpenId(), null);
        if (oldWxUser != null) {
            return convertToVO(oldWxUser);
        }

        StudyUserEntity userEntity = buildStudyUserEntity(wxUserRegisterDTO);
        userMapper.insert(userEntity);

        StudyWxUserEntity wxUserEntity = buildStudyWxUserEntity(wxUserRegisterDTO, userEntity.getId());
        studyWxUserMapper.insert(wxUserEntity);

        return convertToVO(wxUserEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWxInfoAfter(String mobile, String wxUserId) {
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(wxUserId)) {
            return;
        }

        StudyWxUserEntity wxUserEntity = studyWxUserMapper.selectById(wxUserId);
        if (wxUserEntity == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        wxUserEntity.setMobile(mobile);
        wxUserEntity.setUpdatedAt(now);
        studyWxUserMapper.updateById(wxUserEntity);

        StudyUserEntity userEntity = userMapper.selectById(wxUserEntity.getStudyUserId());
        if (userEntity != null && StringUtils.isBlank(userEntity.getMobile())) {
            userEntity.setMobile(mobile);
            userEntity.setUpdatedAt(now);
            userMapper.updateById(userEntity);
        }
    }

    private StudyWxUserEntity buildStudyWxUserEntity(StudyWxUserRegisterDTO registerDTO, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        StudyWxUserEntity wxUserEntity = new StudyWxUserEntity();
        wxUserEntity.setStudyUserId(userId);
        wxUserEntity.setAppId(registerDTO.getAppId());
        wxUserEntity.setOpenId(registerDTO.getOpenId());
        wxUserEntity.setUnionId(registerDTO.getUnionId());
        wxUserEntity.setSessionKey(registerDTO.getSessionKey());
        wxUserEntity.setMobile(registerDTO.getMobile());
        wxUserEntity.setNickname(StringUtils.defaultIfBlank(registerDTO.getNickName(), "微信用户"));
        wxUserEntity.setAvatarUrl(registerDTO.getAvatarUrl());
        wxUserEntity.setStatus(Integer.valueOf(DicEnum.WxUserStatusEnum.DEFAULT.getCode()));
        wxUserEntity.setLastLoginAt(now);
        wxUserEntity.setCreatedAt(now);
        wxUserEntity.setUpdatedAt(now);
        return wxUserEntity;
    }

    private StudyUserEntity buildStudyUserEntity(StudyWxUserRegisterDTO registerDTO) {
        LocalDateTime now = LocalDateTime.now();

        StudyUserEntity userEntity = new StudyUserEntity();
        userEntity.setUsername("wx_" + registerDTO.getOpenId());
        userEntity.setPassword("");
        userEntity.setMobile(registerDTO.getMobile());
        userEntity.setNickname(StringUtils.defaultIfBlank(registerDTO.getNickName(), "微信用户"));
        userEntity.setAvatarUrl(registerDTO.getAvatarUrl());
        userEntity.setStatus(0);
        userEntity.setCreatedAt(now);
        userEntity.setUpdatedAt(now);
        return userEntity;
    }


    private StudyWxUserResponse convertToVO(StudyWxUserEntity entity) {
        StudyWxUserResponse vo = BeanUtil.shallowCopy(entity, StudyWxUserResponse.class);
        vo.setWxuserId(String.valueOf(entity.getId()));
        vo.setStudyUserId(String.valueOf(entity.getStudyUserId()));
        vo.setOpenid(entity.getOpenId());
        vo.setUnionid(entity.getUnionId());
        vo.setMobile(entity.getMobile());
        vo.setNickName(entity.getNickname());
        vo.setPhoto(entity.getAvatarUrl());
        vo.setWxuserStatus(String.valueOf(entity.getStatus()));
        return vo;
    }
}
