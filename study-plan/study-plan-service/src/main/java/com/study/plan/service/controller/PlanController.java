package com.study.plan.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.plan.api.PlanApi;
import com.study.plan.api.dto.CheckInRequest;
import com.study.plan.api.dto.ExamPlanCreateRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class PlanController implements PlanApi {

    private final AtomicLong planIdGenerator = new AtomicLong(1);
    private final AtomicLong taskIdGenerator = new AtomicLong(1);
    private final Map<Long, Map<String, Object>> plans = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, Object>> tasks = new ConcurrentHashMap<>();

    @Override
    @GetMapping("/plans/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("service", ServiceNamesConstant.PLAN, "status", "UP"));
    }

    @Override
    @PostMapping("/plans")
    public ApiResponse<Map<String, Object>> createPlan(@RequestBody ExamPlanCreateRequest request) {
        long planId = planIdGenerator.getAndIncrement();
        LocalDate examDate = request.examDate() == null ? LocalDate.now().plusMonths(3) : request.examDate();
        Map<String, Object> plan = Map.of(
                "id", planId,
                "examName", request.examName(),
                "examDate", examDate,
                "dailyStudyMinutes", request.dailyStudyMinutes() == null ? 60 : request.dailyStudyMinutes(),
                "level", request.level() == null ? "unknown" : request.level(),
                "status", "ACTIVE"
        );
        plans.put(planId, plan);

        createTask(planId, "梳理考试大纲与资料目录", LocalDate.now());
        createTask(planId, "完成第一轮核心知识点学习", LocalDate.now().plusDays(1));
        createTask(planId, "完成阶段复盘与错题整理", LocalDate.now().plusDays(2));

        return ApiResponse.success(plan);
    }

    @Override
    @GetMapping("/plans")
    public ApiResponse<List<Map<String, Object>>> listPlans() {
        return ApiResponse.success(plans.values().stream()
                .sorted(Comparator.comparing(item -> (Long) item.get("id")))
                .toList());
    }

    @Override
    @GetMapping("/plans/{planId}/tasks")
    public ApiResponse<List<Map<String, Object>>> listTasks(@PathVariable Long planId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> task : tasks.values()) {
            if (planId.equals(task.get("planId"))) {
                result.add(task);
            }
        }
        result.sort(Comparator.comparing(item -> (Long) item.get("id")));
        return ApiResponse.success(result);
    }

    @Override
    @PostMapping("/checkins")
    public ApiResponse<Map<String, Object>> checkIn(@RequestBody CheckInRequest request) {
        Map<String, Object> task = tasks.get(request.taskId());
        if (task == null) {
            return ApiResponse.fail(404, "task not found");
        }
        Map<String, Object> checkedTask = new ConcurrentHashMap<>(task);
        checkedTask.put("completed", Boolean.TRUE.equals(request.completed()));
        checkedTask.put("note", request.note() == null ? "" : request.note());
        checkedTask.put("checkedAt", LocalDate.now());
        tasks.put(request.taskId(), checkedTask);
        return ApiResponse.success(checkedTask);
    }

    private void createTask(Long planId, String title, LocalDate dueDate) {
        long taskId = taskIdGenerator.getAndIncrement();
        tasks.put(taskId, new ConcurrentHashMap<>(Map.of(
                "id", taskId,
                "planId", planId,
                "title", title,
                "dueDate", dueDate,
                "completed", false
        )));
    }
}
