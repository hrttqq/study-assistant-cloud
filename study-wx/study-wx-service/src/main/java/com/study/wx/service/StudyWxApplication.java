package com.study.wx.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.study.wx.service.config.WxMiniProgramProperties;

/**
 * 微信模块
 */
@EnableConfigurationProperties(WxMiniProgramProperties.class)
@SpringBootApplication(scanBasePackages = "com.study")
public class StudyWxApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyWxApplication.class, args);
    }
}
