
package com.study.exam.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.common.core.context.UserContext;
import com.study.common.core.model.ResultMode;
import com.study.common.core.model.TokenInfo;
import com.study.common.core.utils.JwtUtil;
import com.study.exam.api.dto.UserExamInitializeRequest;
import com.study.exam.api.response.*;
import com.study.exam.api.service.UserExamService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/exam/user")
public class UserExamController {

    @Autowired
    private UserExamService userExamService;


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

}
