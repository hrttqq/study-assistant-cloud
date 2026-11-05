
package com.study.exam.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_exam_category")
public class ExamCategoryEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String categoryCode;
    private String categoryName;
    private String description;
    private String iconUrl;
    private Integer sort;
    private Integer status;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
