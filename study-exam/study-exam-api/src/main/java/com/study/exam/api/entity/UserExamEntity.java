
package com.study.exam.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("study_user_exam")
public class UserExamEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String studyUserId;

    private Long examId;

    private String examName;

    private Integer examYear;

    private LocalDate examDate;

    /** JSON 字符串 */
    private String attributes;

    /** 1备考中 2已完成 3已取消 */
    private Integer status;

    /** 1当前目标 0非当前目标 */
    private Integer isCurrent;

    /** INITIAL / MANUAL */
    private String source;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
