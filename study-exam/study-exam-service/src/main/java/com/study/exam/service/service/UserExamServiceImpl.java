
package com.study.exam.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.exam.api.dto.UserExamInitializeRequest;
import com.study.exam.api.entity.ExamAttributeEntity;
import com.study.exam.api.entity.ExamCategoryEntity;
import com.study.exam.api.entity.ExamEntity;
import com.study.exam.api.entity.UserExamEntity;
import com.study.exam.api.response.UserExamResponse;
import com.study.exam.api.service.UserExamService;
import com.study.exam.service.mapper.ExamAttributeMapper;
import com.study.exam.service.mapper.ExamCategoryMapper;
import com.study.exam.service.mapper.ExamMapper;
import com.study.exam.service.mapper.UserExamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserExamServiceImpl implements UserExamService {

    @Autowired
    private UserExamMapper userExamMapper;
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private ExamCategoryMapper categoryMapper;
    @Autowired
    private ExamAttributeMapper attributeMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public UserExamResponse initialize(String studyUserId, UserExamInitializeRequest request) {

        checkUserId(studyUserId);

        // 幂等：首次初始化成功后，重复请求返回原记录
        UserExamEntity existing = findInitial(studyUserId);
        if (existing != null) {
            return toResponse(existing);
        }

        if (request == null || request.getExamId() == null) {
            throw new IllegalArgumentException("请选择考试项目");
        }

        ExamEntity exam = examMapper.selectById(request.getExamId());
        if (exam == null ||
                !Integer.valueOf(1).equals(exam.getStatus())) {
            throw new IllegalArgumentException("考试项目不存在或已下架");
        }

        ExamCategoryEntity category =
                categoryMapper.selectById(exam.getCategoryId());

        if (category == null ||
                !Integer.valueOf(1).equals(category.getStatus())) {
            throw new IllegalArgumentException("考试分类已停用");
        }

        validateDate(request);
        Map<String, Object> attributes = validateAttributes(
                exam.getId(), request.getAttributes());

        UserExamEntity entity = new UserExamEntity();
        entity.setStudyUserId(studyUserId);
        entity.setExamId(exam.getId());
        entity.setExamName(exam.getExamName());
        entity.setExamYear(request.getExamYear());
        entity.setExamDate(request.getExamDate());
        entity.setAttributes(writeJson(attributes));
        entity.setStatus(1);
        entity.setIsCurrent(1);
        entity.setSource("INITIAL");

        try {
            userExamMapper.insert(entity);
            return toResponse(entity);
        } catch (DuplicateKeyException ex) {
            // 并发提交：由数据库唯一索引保证不重复初始化
            UserExamEntity initial = findInitial(studyUserId);
            if (initial != null) {
                return toResponse(initial);
            }

            // 可能是已有 MANUAL 当前目标等其他唯一约束冲突
            throw ex;
        }
    }

    @Override
    public UserExamResponse getCurrent(String studyUserId) {
        checkUserId(studyUserId);

        LambdaQueryWrapper<UserExamEntity> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(UserExamEntity::getStudyUserId, studyUserId)
                .eq(UserExamEntity::getIsCurrent, 1)
                .last("LIMIT 1");

        return toResponse(userExamMapper.selectOne(wrapper));
    }

    @Override
    public boolean isInitialized(String studyUserId) {
        checkUserId(studyUserId);
        return findInitial(studyUserId) != null;
    }

    private UserExamEntity findInitial(String studyUserId) {
        LambdaQueryWrapper<UserExamEntity> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(UserExamEntity::getStudyUserId, studyUserId)
                .eq(UserExamEntity::getSource, "INITIAL")
                .last("LIMIT 1");

        return userExamMapper.selectOne(wrapper);
    }

    private void checkUserId(String studyUserId) {
        if (studyUserId == null || studyUserId.isBlank()) {
            throw new IllegalArgumentException("用户身份不能为空");
        }
    }

    private void validateDate(UserExamInitializeRequest request) {
        Integer year = request.getExamYear();
        LocalDate date = request.getExamDate();

        if (year != null && (year < 2000 || year > 9999)) {
            throw new IllegalArgumentException("考试年份不合法");
        }

        if (date != null && date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("考试日期不能早于今天");
        }

        if (year != null && date != null &&
                !year.equals(date.getYear())) {
            throw new IllegalArgumentException(
                    "考试年份与考试日期不一致");
        }
    }

    private Map<String, Object> validateAttributes(
            Long examId, Map<String, Object> submitted) {

        Map<String, Object> values =
                submitted == null ? Collections.emptyMap() : submitted;

        List<ExamAttributeEntity> definitions =
                attributeMapper.selectList(
                        new LambdaQueryWrapper<ExamAttributeEntity>()
                                .eq(ExamAttributeEntity::getExamId, examId)
                                .eq(ExamAttributeEntity::getStatus, 1));

        Map<String, ExamAttributeEntity> definitionMap =
                definitions.stream().collect(Collectors.toMap(
                        ExamAttributeEntity::getAttributeCode,
                        item -> item));

        for (String code : values.keySet()) {
            if (!definitionMap.containsKey(code)) {
                throw new IllegalArgumentException(
                        "不支持的考试属性：" + code);
            }
        }

        for (ExamAttributeEntity definition : definitions) {
            Object value = values.get(definition.getAttributeCode());

            if (Boolean.TRUE.equals(definition.getRequired()) &&
                    (value == null ||
                            value instanceof String s && s.isBlank())) {
                throw new IllegalArgumentException(
                        "请填写：" + definition.getAttributeName());
            }

            if (value == null) {
                continue;
            }

            String type = definition.getInputType();
            if ("text".equals(type) || "select".equals(type)) {
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                            definition.getAttributeName() + "必须为字符串");
                }
            } else if ("number".equals(type)) {
                if (!(value instanceof Number)) {
                    throw new IllegalArgumentException(
                            definition.getAttributeName() + "必须为数字");
                }
            } else {
                throw new IllegalArgumentException(
                        "不支持的属性类型：" + type);
            }

            if ("select".equals(type) &&
                    definition.getOptionsJson() != null) {
                try {
                    List<Map<String, Object>> options =
                            objectMapper.readValue(
                                    definition.getOptionsJson(),
                                    new TypeReference<>() {});

                    boolean matched = options.stream().anyMatch(
                            option -> Objects.equals(
                                    option.get("value"), value));

                    if (!matched) {
                        throw new IllegalArgumentException(
                                definition.getAttributeName()
                                        + "的选项不合法");
                    }
                } catch (IllegalArgumentException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new IllegalStateException(
                            "考试属性选项配置错误", ex);
                }
            }
        }

        return new HashMap<>(values);
    }

    private String writeJson(Map<String, Object> attributes) {
        try {
            return objectMapper.writeValueAsString(attributes);
        } catch (Exception ex) {
            throw new IllegalArgumentException("考试属性格式错误", ex);
        }
    }

    private UserExamResponse toResponse(UserExamEntity entity) {
        if (entity == null) {
            return null;
        }

        UserExamResponse response = new UserExamResponse();
        response.setId(entity.getId());
        response.setStudyUserId(entity.getStudyUserId());
        response.setExamId(entity.getExamId());
        response.setExamName(entity.getExamName());
        response.setExamYear(entity.getExamYear());
        response.setExamDate(entity.getExamDate());
        response.setStatus(entity.getStatus());
        response.setIsCurrent(entity.getIsCurrent());
        response.setSource(entity.getSource());
        response.setCreateTime(entity.getCreateTime());

        try {
            if (entity.getAttributes() == null) {
                response.setAttributes(Collections.emptyMap());
            } else {
                response.setAttributes(objectMapper.readValue(
                        entity.getAttributes(),
                        new TypeReference<Map<String, Object>>() {}));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("备考属性解析失败", ex);
        }

        return response;
    }
}
