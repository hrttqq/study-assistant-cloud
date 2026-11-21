package com.study.file.service.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QiniuProperties.class)
public class StudyFileConfig {
}
