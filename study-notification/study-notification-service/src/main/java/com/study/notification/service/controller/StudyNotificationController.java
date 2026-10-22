package com.study.notification.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.common.core.model.ResultMode;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notification")
public class StudyNotificationController {

    @GetMapping("/health")
    public ResultMode<Map<String, String>> health() {
        ResultMode<Map<String, String>> resultMode = new ResultMode<>();
        Map<String, String> result = new HashMap<>();
        result.put("service", ServiceNamesConstant.NOTIFICATION);
        result.put("status", "UP");
        resultMode.setData(result);
        return resultMode;
    }
}
