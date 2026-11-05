package com.study.exam.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.exam.api.entity.ExamAttributeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamAttributeMapper
        extends BaseMapper<ExamAttributeEntity> {
}
