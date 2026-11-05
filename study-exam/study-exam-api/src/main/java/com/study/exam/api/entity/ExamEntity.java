
package com.study.exam.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_exam")
public class ExamEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long categoryId;
    private String examCode;
    private String examName;
    private String description;
    private String iconUrl;
    private Integer sort;
    private Integer status;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
