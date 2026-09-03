package com.study.auth.service.controller;

import com.study.auth.api.AuthApi;
import com.study.auth.api.dto.LoginRequest;
import com.study.auth.api.dto.RegisterRequest;
import com.study.common.core.constant.ServiceNamesConstant;
import com.study.common.core.model.ResultMode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthApi {

    private final AtomicLong userIdGenerator = new AtomicLong(1);
    private final Map<String, Map<String, Object>> users = new ConcurrentHashMap<>();

    @Override
    @GetMapping("/health")
    public ResultMode<Map<String, String>> health() {
        return new ResultMode(Map.of("service", ServiceNamesConstant.AUTH, "status", "UP"));
    }
}
