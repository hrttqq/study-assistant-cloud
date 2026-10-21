package com.study.exam.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.study")
public class StudyExamApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyExamApplication.class, args);
    }
}
