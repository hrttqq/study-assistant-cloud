package com.study.plan.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习计划实体。
 */
@Data
@TableName("study_plan")
public class StudyPlanEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String studyUserId;

    private Long userExamId;

    private Long examCategoryId;

    private String planName;

    private String planType;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer dailyStudyMinutes;

    private Integer status;

    private String source;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
