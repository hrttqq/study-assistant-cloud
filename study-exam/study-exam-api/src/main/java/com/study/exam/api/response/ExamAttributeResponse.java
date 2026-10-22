package com.study.exam.api.response;

import lombok.Data;

@Data
public class ExamAttributeResponse {
    private Long id;

    private Long examId;

    private String attributeCode;

    private String attributeName;

    private String inputType;

    /**
     * JSON格式的选项配置
     */
    private String optionsJson;

    /**
     * 是否必填
     */
    private Boolean required;

    private Integer sort;
}
