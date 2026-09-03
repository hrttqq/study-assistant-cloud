package com.study.auth.api.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户与角色关联关系entity
 */
@Data
@TableName("study_user_role")
public class StudyUserRoleEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String roleCode;
    private LocalDateTime createdAt;
}
