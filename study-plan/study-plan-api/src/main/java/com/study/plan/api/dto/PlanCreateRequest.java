package com.study.plan.api.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 创建学习计划请求。
 */
@Data
public class PlanCreateRequest {
    /** 用户考试记录 ID */
    private Long userExamId;

    /** 学习计划名称 */
    private String planName;

    /** 学习计划说明 */
    private String description;

    /** 计划开始日期 */
    private LocalDate startDate;

    /** 计划结束日期 */
    private LocalDate endDate;

    /** 每日目标学习时长 */
    private Integer dailyStudyMinutes;
}
