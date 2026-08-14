package com.study.user.api;

import com.study.common.core.model.ResultMode;

import java.util.Map;

public interface UserApi {

    ResultMode<Map<String, String>> health();

    ApiResponse<Map<String, Object>> currentUser();

    ApiResponse<Map<String, Object>> getUserById(Long id);

    ApiResponse<Map<String, Object>> getUserByWxOpenId(String openId);

    ApiResponse<Map<String, Object>> registerOrLoginByWx(WxUserLoginRequest request);
}
