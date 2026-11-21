package com.study.file.api.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyFileResponse {
    private Long id;

    private String studyUserId;

    private String originalName;

    private String extension;

    private String contentType;

    private Long fileSize;

    private Integer status;

    private LocalDateTime createTime;
}
