package com.study.auth.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.common.core.model.ResultMode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class StudyAuthController {


    @GetMapping("/health")
    public ResultMode<Map<String, String>> health() {
        ResultMode<Map<String, String>> resultMode = new ResultMode<>();
        Map<String, String> result = new HashMap<>();
        result.put("service", ServiceNamesConstant.AUTH);
        result.put("status", "UP");
        resultMode.setData(result);
        return resultMode;
    }
}
