package com.study.wx.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("微信用户登录响应信息")
public class WxUserLoginResponseVO {
    @ApiModelProperty("用户token")
    private String token;

    @ApiModelProperty(value = "微信用户ID")
    private String studyWxUserId;

    @ApiModelProperty(value = "内部用户ID")
    private String studyUserId;

    @ApiModelProperty(value = "是否完成初始化数据")
    private Boolean initialized;
}
