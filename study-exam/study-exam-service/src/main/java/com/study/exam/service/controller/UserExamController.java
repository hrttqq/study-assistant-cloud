
package com.study.exam.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.common.core.context.UserContext;
import com.study.common.core.model.ResultMode;
import com.study.common.core.model.TokenInfo;
import com.study.common.core.utils.JwtUtil;
import com.study.exam.api.dto.UserExamInitializeRequest;
import com.study.exam.api.response.UserExamResponse;
import com.study.exam.api.service.UserExamService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exam/user")
public class UserExamController {

    private final UserExamService userExamService;
    private final ObjectMapper objectMapper;

    @PostMapping("/initialize")
    public ResultMode<UserExamResponse> initialize(@RequestBody UserExamInitializeRequest request) {

        String userId = UserContext.getCurrentUser().getUserId();


        UserExamResponse data =
                userExamService.initialize(userId, request);

        ResultMode<UserExamResponse> result = new ResultMode<>();
        result.setData(data);
        return result;
    }

    @GetMapping("/current")
    public ResultMode<UserExamResponse> current() {

        String userId =
                UserContext.getCurrentUser().getUserId();

        ResultMode<UserExamResponse> result = new ResultMode<>();
        result.setData(userExamService.getCurrent(userId));
        return result;
    }

    @GetMapping("/initialized")
    public ResultMode<Map<String, Boolean>> initialized() {

        String userId =
                UserContext.getCurrentUser().getUserId();

        ResultMode<Map<String, Boolean>> result = new ResultMode<>();
        result.setData(Map.of(
                "initialized",
                userExamService.isInitialized(userId)
        ));
        return result;
    }
}
