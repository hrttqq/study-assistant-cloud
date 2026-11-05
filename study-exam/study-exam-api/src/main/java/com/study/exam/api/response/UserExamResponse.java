
package com.study.exam.api.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class UserExamResponse {

    private Long id;

    private String studyUserId;

    private Long examId;

    private String examName;

    private Integer examYear;

    private LocalDate examDate;

    private Map<String, Object> attributes;

    private Integer status;

    private Integer isCurrent;

    private String source;

    private LocalDateTime createTime;
}
