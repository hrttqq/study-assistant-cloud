package com.study.plan.api.feign;

import com.study.common.core.model.ResultMode;
import com.study.exam.api.response.UserExamResponse;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "study-exam-service", url = "${studyExam.host}")
public interface StudyExamApiService {

    ResultMode<UserExamResponse> getById(Long userExamId);
}