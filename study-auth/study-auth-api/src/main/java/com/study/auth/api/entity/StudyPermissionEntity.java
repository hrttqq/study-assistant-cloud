package com.study.auth.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色类entity
 */
@Data
@TableName("study_permission")
public class StudyPermissionEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private String resource;
    private String action;
    private String description;
    private LocalDateTime createdAt;
}
