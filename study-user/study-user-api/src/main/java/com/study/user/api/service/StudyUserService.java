package com.study.user.api.service;

import com.study.user.api.response.StudyUserResponse;

public interface StudyUserService {
    StudyUserResponse getModelByLoginId(String studyUserId);

}
