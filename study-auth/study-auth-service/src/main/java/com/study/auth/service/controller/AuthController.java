package com.study.auth.service.controller;

import com.study.auth.api.AuthApi;
import com.study.auth.api.dto.LoginRequest;
import com.study.auth.api.dto.RegisterRequest;
import com.study.common.core.constant.ServiceNamesConstant;
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
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("service", ServiceNamesConstant.AUTH, "status", "UP"));
    }

    @Override
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        if (users.containsKey(request.username())) {
            return ApiResponse.fail(409, "username already exists");
        }
        Map<String, Object> user = Map.of(
                "id", userIdGenerator.getAndIncrement(),
                "username", request.username(),
                "email", request.email() == null ? "" : request.email(),
                "memberLevel", "FREE"
        );
        users.put(request.username(), user);
        return ApiResponse.success(user);
    }

    @Override
    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@RequestBody LoginRequest request) {
        users.computeIfAbsent(request.username(), username -> Map.of(
                "id", userIdGenerator.getAndIncrement(),
                "username", username,
                "email", "",
                "memberLevel", "FREE"
        ));
        return ApiResponse.success(Map.of(
                "username", request.username(),
                "token", "mock-token"
        ));
    }
}
