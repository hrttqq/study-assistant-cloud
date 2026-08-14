package com.study.user.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_wx_user")
public class StudyWxUserEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String appId;
    private String openId;
    private String unionId;
    private String sessionKey;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private Integer status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
