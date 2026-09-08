package com.study.auth.service;

import com.study.common.core.utils.JwtUtil;
import com.study.common.core.utils.RedisUtil;
import com.study.common.core.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@MapperScan("com.study.auth.service.mapper")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.study")
public class StudyAuthApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(StudyAuthApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
        JwtUtil.getInstance().init(context);
        //RedisUtil.initialPool();
        log.info("------StudyAuthApplication Start success-------");
    }
}
