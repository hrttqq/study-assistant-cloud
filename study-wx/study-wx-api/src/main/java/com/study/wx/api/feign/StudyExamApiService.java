package com.study.wx.api.feign;

import com.study.common.core.model.ResultMode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 调用考试服务，查询用户备考目标初始化状态。
 */
@FeignClient(name = "study-exam-service", url = "${studyExam.host}")
public interface StudyExamApiService {

    @GetMapping("/api/studyExam/user/initialized")
    Map<String, Boolean> initialized(@RequestParam("studyUserId") String studyUserId);
}
