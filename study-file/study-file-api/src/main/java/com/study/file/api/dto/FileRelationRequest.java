package com.study.file.api.dto;


import lombok.Data;

@Data
public class FileRelationRequest {

    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    @NotNull(message = "业务ID不能为空")
    private Long businessId;
}
