
package com.study.exam.api.service;

import com.study.exam.api.dto.UserExamInitializeRequest;
import com.study.exam.api.response.UserExamResponse;

public interface UserExamService {

    /**
     * 用户首次初始化备考目标
     */
    UserExamResponse initialize(String studyUserId, UserExamInitializeRequest request);

    /**
     * 获取用户当前备考目标
     */
    UserExamResponse getCurrent(String studyUserId);

    /**
     * 是否已经完成首次初始化
     */
    boolean isInitialized(String studyUserId);
}
