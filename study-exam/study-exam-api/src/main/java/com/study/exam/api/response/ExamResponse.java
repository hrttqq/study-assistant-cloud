package com.study.exam.api.response;

import lombok.Data;

@Data
public class ExamResponse {
    private Long id;

    private Long categoryId;

    private String examCode;

    private String examName;

    private String description;

    private String iconUrl;

    private Integer sort;
}
