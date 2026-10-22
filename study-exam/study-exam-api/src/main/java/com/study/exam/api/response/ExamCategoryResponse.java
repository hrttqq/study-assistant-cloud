package com.study.exam.api.response;

import lombok.Data;

@Data
public class ExamCategoryResponse {
    private Long id;

    private String categoryCode;

    private String categoryName;

    private String description;

    private String iconUrl;

    private Integer sort;
}
