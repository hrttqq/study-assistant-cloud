package com.study.user.service;

import com.study.common.core.utils.RedisUtil;
import com.study.common.core.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@MapperScan("com.study.user.service.mapper")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.study")
public class StudyUserApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(StudyUserApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
        RedisUtil.initialPool();
        log.info("------StudyUserApplication Start success-------");
    }
}
