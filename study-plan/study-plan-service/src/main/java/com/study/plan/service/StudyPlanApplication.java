package com.study.plan.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.study")
@EnableFeignClients(basePackages = "com.study.plan.api.feign")
public class StudyPlanApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyPlanApplication.class, args);
    }
}
