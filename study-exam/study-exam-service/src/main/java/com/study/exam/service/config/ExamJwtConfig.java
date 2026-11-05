
package com.study.exam.service.config;

import com.study.common.core.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class ExamJwtConfig {

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void initialize() {
        JwtUtil.getInstance().init(
                (ConfigurableApplicationContext) applicationContext);
    }
}
