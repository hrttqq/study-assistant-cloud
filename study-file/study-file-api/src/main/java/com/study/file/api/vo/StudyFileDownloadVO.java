package com.study.file.api.vo;

import lombok.Data;

@Data
public class StudyFileDownloadVO {
    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 临时下载地址
     */
    private String downloadUrl;

    /**
     * 有效时间（秒）
     */
    private Integer expireSeconds;
}
