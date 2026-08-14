package com.study.member.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_user_member")
public class UserMemberEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String planCode;
    private String source;
    private LocalDateTime startedAt;
    private LocalDateTime expiredAt;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
