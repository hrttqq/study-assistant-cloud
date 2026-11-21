package com.study.file.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("study_file_relation")
public class StudyFileRelationEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long fileId;

    private String studyUserId;

    private String businessType;

    private Long businessId;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    private LocalDateTime createTime;
}
