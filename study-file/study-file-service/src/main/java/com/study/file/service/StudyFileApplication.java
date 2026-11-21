package com.study.file.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages="com.study")
@MapperScan("com.study.file.service.mapper")
public class StudyFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyFileApplication.class, args);
    }
}
