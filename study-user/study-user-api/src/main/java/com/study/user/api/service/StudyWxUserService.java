package com.study.user.api.service;

import com.study.user.api.dto.StudyWxUserRegisterDTO;
import com.study.user.api.response.StudyWxUserResponse;

public interface StudyWxUserService {
    StudyWxUserResponse getOpenId(String openId, String wxUserStatus);

    StudyWxUserResponse createStudyWxUser(StudyWxUserRegisterDTO wxUserRegisterDTO);
}
