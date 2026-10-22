package com.study.plan.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.common.core.model.ResultMode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/plan")
public class StudyPlanController {


    @GetMapping("/health")
    public ResultMode<Map<String, String>> health() {
        ResultMode<Map<String, String>> resultMode = new ResultMode<>();
        Map<String, String> result = new HashMap<>();
        result.put("service", ServiceNamesConstant.PLAN);
        result.put("status", "UP");
        resultMode.setData(result);
        return resultMode;
    }

}
