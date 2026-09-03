package com.study.user.api.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "系统用户响应信息")
public class StudyUserResponse {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "内部用户ID", name = "userBaseId")
    private String studyUserId;

    @ApiModelProperty(value = "用户名", name = "username")
    private String username;

    @ApiModelProperty(value = "手机号", name = "mobile")
    private String mobile;

    @ApiModelProperty(value = "昵称", name = "nickname")
    private String nickname;

    @ApiModelProperty(value = "邮箱", name = "email")
    private String email;

    @ApiModelProperty(value = "头像", name = "avatarUrl")
    private String avatarUrl;

    @ApiModelProperty(value = "用户状态", name = "status")
    private Integer status;

    @ApiModelProperty(value = "创建时间", name = "createdAt")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间", name = "updatedAt")
    private LocalDateTime updatedAt;
}
