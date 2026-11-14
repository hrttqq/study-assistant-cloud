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

    @GetMapping("/user/initialized")
    public Map<String, Boolean> initialized(@RequestParam String studyUserId) {

        Map<String, Boolean> result = Map.of(
                "initialized",
                userExamService.isInitialized(studyUserId)
        );
        return result;
    }

    @PostMapping("/user/getUserExam")
    UserExamResponse getUserExam(@RequestParam Long userExamId) {
        return userExamService.getById(userExamId);
    }
}
