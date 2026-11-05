
package com.study.exam.api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class UserExamInitializeRequest {

    /** 标准考试项目ID */
    private Long examId;

    /** 考试年份，可不填写 */
    private Integer examYear;

    /** 预计考试日期，可不填写 */
    private LocalDate examDate;

    /** 用户填写的动态属性 */
    private Map<String, Object> attributes;
}
