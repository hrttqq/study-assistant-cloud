package com.study.plan.api.service;

import com.study.plan.api.dto.PlanCreateRequest;
import com.study.plan.api.dto.PlanUpdateRequest;
import com.study.plan.api.response.StudyPlanResponse;

/**
 * 学习计划业务接口。
 */
public interface StudyPlanService {

    StudyPlanResponse create(String userId, PlanCreateRequest request);

    StudyPlanResponse getByUserExam(String userId, Long userExamId);

    StudyPlanResponse update(String userId, Long planId, PlanUpdateRequest request);
}
