package com.study.exam.service.controller.api;

import com.study.common.core.context.UserContext;
import com.study.common.core.model.ResultMode;
import com.study.exam.api.dto.UserExamInitializeRequest;
import com.study.exam.api.response.UserExamResponse;
import com.study.exam.api.service.UserExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/studyExam")
public class StudyExamApiController {

    @Autowired
    private UserExamService userExamService;

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
