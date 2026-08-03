package com.study.user.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.user.api.UserApi;
import com.study.user.api.dto.WxUserLoginRequest;
import com.study.user.service.entity.UserEntity;
import com.study.user.service.mapper.UserMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController implements UserApi {

    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("service", ServiceNamesConstant.USER, "status", "UP"));
    }

    @Override
    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> currentUser() {
        return ApiResponse.success(Map.of(
                "id", 1L,
                "username", "demo-user",
                "nickname", "Demo User"
        ));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getUserById(@PathVariable Long id) {
        UserEntity user = userMapper.selectById(id);
        if (user == null) {
            return ApiResponse.fail(404, "user not found");
        }
        return ApiResponse.success(toUserMap(user));
    }

    @Override
    @GetMapping("/internal/wx/openid/{openId}")
    public ApiResponse<Map<String, Object>> getUserByWxOpenId(@PathVariable String openId) {
        UserEntity user = userMapper.findByWxOpenId(openId);
        if (user == null) {
            return ApiResponse.fail(404, "wx user not found");
        }
        return ApiResponse.success(toUserMap(user));
    }

    @Override
    @PostMapping("/internal/wx/register-or-login")
    public ApiResponse<Map<String, Object>> registerOrLoginByWx(@RequestBody WxUserLoginRequest request) {
        if (request.openId() == null || request.openId().isBlank()) {
            return ApiResponse.fail(400, "openId is required");
        }

        UserEntity user = userMapper.findByWxOpenId(request.openId());
        boolean registered = false;
        if (user == null) {
            user = new UserEntity();
            user.setUsername("wx_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
            user.setNickname(request.nickname() == null || request.nickname().isBlank() ? "微信用户" : request.nickname());
            user.setMemberLevel("FREE");
            user.setWxOpenId(request.openId());
            user.setWxUnionId(request.unionId());
            user.setAvatarUrl(request.avatarUrl());
            userMapper.insert(user);
            registered = true;
        }

        Map<String, Object> result = toUserMap(user);
        result.put("registered", registered);
        return ApiResponse.success(result);
    }

    private Map<String, Object> toUserMap(UserEntity user) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("email", user.getEmail());
        result.put("memberLevel", user.getMemberLevel());
        result.put("wxOpenId", user.getWxOpenId());
        result.put("wxUnionId", user.getWxUnionId());
        result.put("avatarUrl", user.getAvatarUrl());
        return result;
    }
}
