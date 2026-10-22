package com.study.exam.service.service;

import com.study.exam.api.response.ExamAttributeResponse;
import com.study.exam.api.response.ExamCategoryResponse;
import com.study.exam.api.response.ExamResponse;

import java.util.List;

public interface ExamCatalogService {
    List<ExamAttributeResponse> listAttributes(Long examId);

    List<ExamResponse> listExams(Long categoryId);

    List<ExamCategoryResponse> listCategories();

}
