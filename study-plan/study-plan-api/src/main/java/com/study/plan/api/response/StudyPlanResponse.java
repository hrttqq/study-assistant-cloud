package com.study.plan.api.response;

import lombok.Data;

import java.time.LocalDate;

/**
 * 学习计划响应。
 */
@Data
public class StudyPlanResponse {
    private Long id;

    private Long userExamId;

    private String planName;

    private String description;

    private String planType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer dailyStudyMinutes;

    private Integer status;

    private Integer totalTasks;

    private Integer completedTasks;

    private Integer progress;
}
