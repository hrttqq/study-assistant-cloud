package com.study.user.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.common.core.model.ResultMode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class StudyUserController {

    @GetMapping("/health")
    public ResultMode<Map<String, String>> health() {
        ResultMode<Map<String, String>> resultMode = new ResultMode<>();
        Map<String, String> result = new HashMap<>();
        result.put("service", ServiceNamesConstant.USER);
        result.put("status", "UP");
        resultMode.setData(result);
        return resultMode;
    }
}
