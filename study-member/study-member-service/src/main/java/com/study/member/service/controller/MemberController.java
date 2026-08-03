package com.study.member.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.member.api.MemberApi;
import com.study.member.api.dto.UpgradeRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class MemberController implements MemberApi {

    private final AtomicReference<String> currentPlan = new AtomicReference<>("FREE");

    @Override
    @GetMapping("/memberships/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("service", ServiceNamesConstant.MEMBER, "status", "UP"));
    }

    @Override
    @GetMapping("/memberships/plans")
    public ApiResponse<List<Map<String, Object>>> listPlans() {
        return ApiResponse.success(List.of(
                Map.of("code", "FREE", "name", "免费版", "examPlanLimit", 1, "aiEnabled", false),
                Map.of("code", "PRO", "name", "Pro会员", "examPlanLimit", -1, "aiEnabled", true),
                Map.of("code", "PREMIUM", "name", "高级会员", "examPlanLimit", -1, "aiEnabled", true)
        ));
    }

    @Override
    @GetMapping("/memberships/current")
    public ApiResponse<Map<String, Object>> currentMembership() {
        return ApiResponse.success(describePlan(currentPlan.get()));
    }

    @Override
    @PostMapping("/memberships/upgrade")
    public ApiResponse<Map<String, Object>> upgrade(@RequestBody UpgradeRequest request) {
        String planCode = request.planCode() == null ? "FREE" : request.planCode().toUpperCase();
        if (!List.of("FREE", "PRO", "PREMIUM").contains(planCode)) {
            return ApiResponse.fail(400, "unsupported membership plan");
        }
        currentPlan.set(planCode);
        return ApiResponse.success(describePlan(planCode));
    }

    private Map<String, Object> describePlan(String code) {
        return switch (code) {
            case "PRO" -> Map.of(
                    "code", "PRO",
                    "name", "Pro会员",
                    "benefits", List.of("无限考试计划", "大容量资料空间", "AI规划", "AI问答", "学习报告")
            );
            case "PREMIUM" -> Map.of(
                    "code", "PREMIUM",
                    "name", "高级会员",
                    "benefits", List.of("模拟考试", "AI老师", "个性化辅导", "Pro全部权益")
            );
            default -> Map.of(
                    "code", "FREE",
                    "name", "免费版",
                    "benefits", List.of("一个考试计划", "限制资料空间", "限制AI次数")
            );
        };
    }
}
