package com.study.auth.api.service;

import com.study.user.api.response.StudyUserResponse;

public interface StudyAuthService {
    String getJwtToken(StudyUserResponse studyUser, String token);
}
