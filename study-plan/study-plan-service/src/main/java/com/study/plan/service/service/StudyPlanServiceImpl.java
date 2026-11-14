package com.study.plan.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.common.core.model.ResultMode;
import com.study.exam.api.response.UserExamResponse;
import com.study.plan.api.dto.PlanCreateRequest;
import com.study.plan.api.dto.PlanUpdateRequest;
import com.study.plan.api.entity.StudyPlanEntity;
import com.study.plan.api.feign.StudyExamApiService;
import com.study.plan.api.response.StudyPlanResponse;
import com.study.plan.api.service.StudyPlanService;
import com.study.plan.service.client.StudyExamClient;
import com.study.plan.service.mapper.StudyPlanMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

/**
 * 学习计划业务实现。
 */
@Service
@Slf4j
public class StudyPlanServiceImpl implements StudyPlanService {


    @Autowired
    private StudyPlanMapper planMapper;

    @Autowired
    private StudyExamApiService studyExamApiService;

    @Override
    @Transactional
    public StudyPlanResponse create(
            String userId, PlanCreateRequest request) {

        requireUser(userId);

        if (request == null || request.getUserExamId() == null) {
            throw new IllegalArgumentException("请选择考试项目");
        }

        validatePlan(
                request.getPlanName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getDailyStudyMinutes());

        UserExamResponse exam = requireExam(request.getUserExamId());

        if (!userId.equals(exam.getStudyUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "无权访问该考试项目");
        }

        StudyPlanEntity existing =
                findByExam(userId, request.getUserExamId());

        if (existing != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "该考试项目已有学习计划");
        }

        StudyPlanEntity entity = new StudyPlanEntity();
        entity.setStudyUserId(userId);
        entity.setUserExamId(request.getUserExamId());
        entity.setPlanName(request.getPlanName());
        entity.setDescription(request.getDescription());
        entity.setPlanType("CUSTOM");
        entity.setSource("MANUAL");
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setDailyStudyMinutes(request.getDailyStudyMinutes());
        entity.setStatus(1);
        entity.setDeleted(0);

        try {
            planMapper.insert(entity);
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "该考试项目已有学习计划", ex);
        }

        return toResponse(entity);
    }

    @Override
    public StudyPlanResponse getByUserExam(
            String userId, Long userExamId) {

        requireUser(userId);
        requireExam(userExamId);

        StudyPlanEntity entity = findByExam(userId, userExamId);

        // 无计划时返回 null，首页可以显示创建计划入口
        return entity == null ? null : toResponse(entity);
    }

    @Override
    @Transactional
    public StudyPlanResponse update(
            String userId, Long planId,
            PlanUpdateRequest request) {

        requireUser(userId);

        if (request == null) {
            throw new IllegalArgumentException("更新内容不能为空");
        }

        validatePlan(
                request.getPlanName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getDailyStudyMinutes());

        StudyPlanEntity entity = planMapper.selectOne(
                new LambdaQueryWrapper<StudyPlanEntity>()
                        .eq(StudyPlanEntity::getId, planId)
                        .eq(StudyPlanEntity::getStudyUserId, userId));

        if (entity == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "学习计划不存在");
        }

        entity.setPlanName(request.getPlanName());
        entity.setDescription(request.getDescription());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setDailyStudyMinutes(request.getDailyStudyMinutes());

        planMapper.updateById(entity);
        return toResponse(entity);
    }

    private StudyPlanEntity findByExam(
            String userId, Long userExamId) {

        return planMapper.selectOne(
                new LambdaQueryWrapper<StudyPlanEntity>()
                        .eq(StudyPlanEntity::getStudyUserId, userId)
                        .eq(StudyPlanEntity::getUserExamId, userExamId));
    }

    private UserExamResponse requireExam(Long userExamId) {

        if (userExamId == null) {
            throw new IllegalArgumentException("考试项目不能为空");
        }

        ResultMode<UserExamResponse> result =
                studyExamApiService.getById(userExamId);

        if (result == null || !result.getSucceed()
                || result.getData() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "考试项目不存在或无权访问");
        }

        return result.getData();
    }

    private void validatePlan(
            String name, LocalDate start,
            LocalDate end, Integer minutes) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("计划名称不能为空");
        }

        if (start == null || end == null || end.isBefore(start)) {
            throw new IllegalArgumentException("计划日期不合法");
        }

        if (minutes == null || minutes <= 0) {
            throw new IllegalArgumentException("每日学习时长必须大于0");
        }
    }

    private void requireUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "用户未登录");
        }
    }

    private StudyPlanResponse toResponse(StudyPlanEntity entity) {
        StudyPlanResponse response = new StudyPlanResponse();

        response.setId(entity.getId());
        response.setUserExamId(entity.getUserExamId());
        response.setPlanName(entity.getPlanName());
        response.setDescription(entity.getDescription());
        response.setPlanType(entity.getPlanType());
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());
        response.setDailyStudyMinutes(entity.getDailyStudyMinutes());
        response.setStatus(entity.getStatus());

        // 后续由 TaskMapper 提供真实统计
        response.setTotalTasks(0);
        response.setCompletedTasks(0);
        response.setProgress(0);

        return response;
    }
}
