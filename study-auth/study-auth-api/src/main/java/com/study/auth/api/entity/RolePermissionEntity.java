package com.study.auth.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;



@Data
@TableName("study_role_permission")
public class RolePermissionEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roleCode;
    private String permissionCode;
    private LocalDateTime createdAt;
}
