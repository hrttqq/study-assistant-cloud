
package com.study.exam.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.exam.api.entity.ExamAttributeEntity;
import com.study.exam.api.entity.ExamCategoryEntity;
import com.study.exam.api.entity.ExamEntity;
import com.study.exam.api.response.ExamAttributeResponse;
import com.study.exam.api.response.ExamCategoryResponse;
import com.study.exam.api.response.ExamResponse;
import com.study.exam.api.service.ExamCatalogService;
import com.study.exam.service.mapper.ExamAttributeMapper;
import com.study.exam.service.mapper.ExamCategoryMapper;
import com.study.exam.service.mapper.ExamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamCatalogServiceImpl
        implements ExamCatalogService {

    private final ExamCategoryMapper categoryMapper;
    private final ExamMapper examMapper;
    private final ExamAttributeMapper attributeMapper;

    /**
     * 查询所有启用的考试分类
     */
    @Override
    public List<ExamCategoryResponse> listCategories() {

        LambdaQueryWrapper<ExamCategoryEntity> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(ExamCategoryEntity::getStatus, 1)
                .orderByAsc(ExamCategoryEntity::getSort)
                .orderByAsc(ExamCategoryEntity::getId);

        return categoryMapper.selectList(wrapper)
                .stream()
                .map(entity -> {
                    ExamCategoryResponse response =
                            new ExamCategoryResponse();
                    BeanUtils.copyProperties(entity, response);
                    return response;
                })
                .toList();
    }

    /**
     * 根据分类查询考试项目
     */
    @Override
    public List<ExamResponse> listExams(Long categoryId) {

        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException(
                    "考试分类ID不能为空");
        }

        // 禁止通过已禁用的分类查询考试项目
        ExamCategoryEntity category =
                categoryMapper.selectById(categoryId);

        if (category == null ||
                !Integer.valueOf(1).equals(category.getStatus())) {
            return List.of();
        }

        LambdaQueryWrapper<ExamEntity> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(ExamEntity::getCategoryId, categoryId)
                .eq(ExamEntity::getStatus, 1)
                .orderByAsc(ExamEntity::getSort)
                .orderByAsc(ExamEntity::getId);

        return examMapper.selectList(wrapper)
                .stream()
                .map(entity -> {
                    ExamResponse response = new ExamResponse();
                    BeanUtils.copyProperties(entity, response);
                    return response;
                })
                .toList();
    }

    /**
     * 查询考试动态属性
     */
    @Override
    public List<ExamAttributeResponse> listAttributes(Long examId) {

        if (examId == null || examId <= 0) {
            throw new IllegalArgumentException(
                    "考试ID不能为空");
        }

        ExamEntity exam = examMapper.selectById(examId);

        if (exam == null ||
                !Integer.valueOf(1).equals(exam.getStatus())) {
            return List.of();
        }

        ExamCategoryEntity category =
                categoryMapper.selectById(exam.getCategoryId());

        if (category == null ||
                !Integer.valueOf(1).equals(category.getStatus())) {
            return List.of();
        }

        LambdaQueryWrapper<ExamAttributeEntity> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(ExamAttributeEntity::getExamId, examId)
                .eq(ExamAttributeEntity::getStatus, 1)
                .orderByAsc(ExamAttributeEntity::getSort)
                .orderByAsc(ExamAttributeEntity::getId);

        return attributeMapper.selectList(wrapper)
                .stream()
                .map(entity -> {
                    ExamAttributeResponse response =
                            new ExamAttributeResponse();
                    BeanUtils.copyProperties(entity, response);
                    return response;
                })
                .toList();
    }
}
