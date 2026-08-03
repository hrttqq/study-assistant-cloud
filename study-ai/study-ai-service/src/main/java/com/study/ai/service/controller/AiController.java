package com.study.ai.service.controller;

import com.study.ai.api.AiApi;
import com.study.ai.api.dto.ChatRequest;
import com.study.ai.api.dto.PlanGenerateRequest;
import com.study.ai.api.dto.SummarizeRequest;
import com.study.common.core.constant.ServiceNamesConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController implements AiApi {

    @Override
    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("service", ServiceNamesConstant.AI, "status", "UP"));
    }

    @Override
    @PostMapping("/chat")
    public ApiResponse<Map<String, String>> chat(@RequestBody ChatRequest request) {
        return ApiResponse.success(Map.of(
                "question", request.message(),
                "answer", "AI service is ready."
        ));
    }

    @Override
    @PostMapping("/summaries")
    public ApiResponse<Map<String, Object>> summarize(@RequestBody SummarizeRequest request) {
        return ApiResponse.success(Map.of(
                "materialId", request.materialId(),
                "summary", "已提炼资料核心考点、 高频知识点和复习建议。",
                "keywords", java.util.List.of("考试大纲", "核心考点", "错题复盘")
        ));
    }

    @Override
    @PostMapping("/plans")
    public ApiResponse<Map<String, Object>> generatePlan(@RequestBody PlanGenerateRequest request) {
        int dailyMinutes = request.dailyStudyMinutes() == null ? 60 : request.dailyStudyMinutes();
        int materialCount = request.materialCount() == null ? 0 : request.materialCount();
        return ApiResponse.success(Map.of(
                "examName", request.examName(),
                "dailyStudyMinutes", dailyMinutes,
                "strategy", "先建立知识框架，再按资料切片推进学习，最后通过错题和模拟题复盘。",
                "weeklyTasks", java.util.List.of(
                        "完成考试大纲梳理",
                        "学习核心资料 " + Math.max(materialCount, 1) + " 份",
                        "完成错题复盘和阶段测验"
                )
        ));
    }
}
