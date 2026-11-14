package com.study.plan.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.common.core.context.UserContext;
import com.study.common.core.model.ResultMode;
import com.study.plan.api.dto.PlanCreateRequest;
import com.study.plan.api.dto.PlanUpdateRequest;
import com.study.plan.api.response.StudyPlanResponse;
import com.study.plan.api.service.StudyPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/plan")
public class StudyPlanController {


    @Autowired
    private StudyPlanService studyPlanService;


    @GetMapping("/health")
    public ResultMode<Map<String, String>> health() {
        ResultMode<Map<String, String>> resultMode = new ResultMode<>();
        Map<String, String> result = new HashMap<>();
        result.put("service", ServiceNamesConstant.PLAN);
        result.put("status", "UP");
        resultMode.setData(result);
        return resultMode;
    }


    /**
     * 创建考试计划接口
     *
     * @param request
     * @return
     */
    @PostMapping
    public ResultMode<StudyPlanResponse> create(
            @RequestBody PlanCreateRequest request) {

        return wrap(studyPlanService.create(currentUserId(), request));
    }

    /**
     * 查询该用户考试计划
     *
     * @param userExamId
     * @return
     */
    @GetMapping("/by-exam/{userExamId}")
    public ResultMode<StudyPlanResponse> getByExam(@PathVariable Long userExamId) {

        return wrap(studyPlanService.getByUserExam(
                currentUserId(), userExamId));
    }

    /**
     * 修改考试计划
     *
     * @param planId
     * @param request
     * @return
     */
    @PutMapping("/{planId}")
    public ResultMode<StudyPlanResponse> update(@PathVariable Long planId, @RequestBody PlanUpdateRequest request) {

        return wrap(studyPlanService.update(
                currentUserId(), planId, request));
    }

    private String currentUserId() {
        String userId = UserContext.getUserId();

        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "用户未登录");
        }

        return userId;
    }

    private ResultMode<StudyPlanResponse> wrap(
            StudyPlanResponse data) {

        ResultMode<StudyPlanResponse> result = new ResultMode<>();
        result.setData(data);
        return result;
    }

}
