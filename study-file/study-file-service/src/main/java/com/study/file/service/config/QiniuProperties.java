package com.study.file.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@Data
@ConfigurationProperties("study.file.qiniu")
public class QiniuProperties {

    private String accessKey;

    private String secretKey;

    private String bucket;

    private String domain;

    /**
     * 下载链接有效期
     */
    private Integer downloadExpireSeconds = 300;

    /**
     * 最大上传文件大小
     */
    private Long maxFileSize = 52428800L;
}
