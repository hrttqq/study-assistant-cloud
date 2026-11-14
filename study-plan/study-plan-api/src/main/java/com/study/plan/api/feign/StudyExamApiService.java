package com.study.plan.api.feign;

import com.study.common.core.model.ResultMode;
import com.study.exam.api.response.UserExamResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "study-exam-service", url = "${studyExam.host}")
public interface StudyExamApiService {

    @GetMapping("/api/studyExam/getUserExam")
    UserExamResponse getUserExam(@RequestParam Long userExamId);
}