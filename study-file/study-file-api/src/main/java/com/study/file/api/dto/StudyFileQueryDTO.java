package com.study.file.api.dto;

import lombok.Data;

@Data
public class StudyFileQueryDTO {
    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务ID
     */
    private Long businessId;
}
