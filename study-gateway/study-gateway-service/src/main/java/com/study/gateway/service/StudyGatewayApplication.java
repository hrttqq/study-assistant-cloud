package com.study.gateway.service;

import com.study.common.core.utils.RedisUtil;
import com.study.common.core.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.study.gateway")
@EnableDiscoveryClient
public class StudyGatewayApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(StudyGatewayApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
        RedisUtil.initialPool();

        log.info("------StudyGatewayApplication Start success-------");
    }
}
