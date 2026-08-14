package com.study.gateway.service;

import com.study.common.core.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.study")
@EnableDiscoveryClient
public class StudyGatewayApplication {

    public static void main(String[] args) {

        SpringApplication.run(StudyGatewayApplication.class, args);
        RedisUtil.initialPool();

        log.info("------StudyGatewayApplication Start success-------");
    }
}
