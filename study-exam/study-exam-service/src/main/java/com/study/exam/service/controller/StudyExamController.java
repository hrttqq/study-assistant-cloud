package com.study.exam.service.controller;


import com.study.common.core.constant.ServiceNamesConstant;
import com.study.common.core.model.ResultMode;
import com.study.exam.api.response.ExamAttributeResponse;
import com.study.exam.api.response.ExamCategoryResponse;
import com.study.exam.api.response.ExamResponse;
import com.study.exam.api.service.ExamCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exam")
public class StudyExamController {

    private final ExamCatalogService examCatalogService;


    @GetMapping("/health")
    public ResultMode<Map<String, String>> health() {
        ResultMode<Map<String, String>> resultMode = new ResultMode<>();
        Map<String, String> result = new HashMap<>();
        result.put("service", ServiceNamesConstant.EXAM);
        result.put("status", "UP");
        resultMode.setData(result);
        return resultMode;
    }


    @GetMapping("/categories")
    public ResultMode<List<ExamCategoryResponse>> categories() {

        List<ExamCategoryResponse> result =
                examCatalogService.listCategories();

        ResultMode<List<ExamCategoryResponse>> response =
                new ResultMode<>();

        response.setData(result);
        return response;
    }


    @GetMapping("/exams")
    public ResultMode<List<ExamResponse>> exams(
            @RequestParam Long categoryId) {

        List<ExamResponse> result =
                examCatalogService.listExams(categoryId);

        ResultMode<List<ExamResponse>> response =
                new ResultMode<>();

        response.setData(result);
        return response;
    }


    @GetMapping("/exams/{examId}/attributes")
    public ResultMode<List<ExamAttributeResponse>> attributes(
            @PathVariable Long examId) {

        List<ExamAttributeResponse> result =
                examCatalogService.listAttributes(examId);

        ResultMode<List<ExamAttributeResponse>> response =
                new ResultMode<>();

        response.setData(result);
        return response;
    }
}
