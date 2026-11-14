package com.study.plan.api.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 更新学习计划请求。
 */
@Data
public class PlanUpdateRequest {
    private String planName;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer dailyStudyMinutes;
}
