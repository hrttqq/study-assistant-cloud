package com.study.exam.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.exam.api.entity.ExamEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamMapper
        extends BaseMapper<ExamEntity> {
}
