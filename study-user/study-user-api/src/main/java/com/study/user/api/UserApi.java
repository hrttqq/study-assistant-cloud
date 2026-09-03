package com.study.user.api;

import com.study.common.core.model.ResultMode;

import java.util.Map;

public interface UserApi {

    ResultMode<Map<String, String>> health();
}
