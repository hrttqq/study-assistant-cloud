package com.study.file.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_file")
public class StudyFileEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String studyUserId;

    private String originalName;

    private String fileKey;

    private String bucketName;

    private String storageProvider;

    private String extension;

    private String contentType;

    private Long fileSize;

    private String sha256;

    private Integer status;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
