
package com.study.exam.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_exam_attribute")
public class ExamAttributeEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long examId;
    private String attributeCode;
    private String attributeName;
    private String inputType;
    private String optionsJson;
    private Boolean required;
    private Integer sort;
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
