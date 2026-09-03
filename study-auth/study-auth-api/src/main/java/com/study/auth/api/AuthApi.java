package com.study.auth.api;

import com.study.auth.api.dto.LoginRequest;
import com.study.auth.api.dto.RegisterRequest;
import com.study.common.core.model.ResultMode;

import java.util.Map;

public interface AuthApi {

    ResultMode<Map<String, String>> health();
}
