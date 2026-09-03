package com.study.wx.service;

import com.study.common.core.utils.RedisUtil;
import com.study.common.core.utils.SpringContextUtil;
import com.study.wx.service.config.WxMiniProgramProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 微信模块
 */
@Slf4j
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.study.wx.service.feign")
@EnableConfigurationProperties(WxMiniProgramProperties.class)
@SpringBootApplication(scanBasePackages = "com.study")
public class StudyWxApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(StudyWxApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
        RedisUtil.initialPool();
        log.info("------StudyWxApplication Start success-------");
    }
}
