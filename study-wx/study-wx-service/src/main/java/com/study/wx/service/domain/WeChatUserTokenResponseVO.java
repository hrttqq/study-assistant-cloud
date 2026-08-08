package com.study.wx.service.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户TOKEN返回信息
 *
 * @author gq
 * @date 2022/6/22 14:27
 */
@ApiModel("用户TOKEN返回信息")
public class WeChatUserTokenResponseVO {

    @ApiModelProperty("用户token")
    private String access_token;

    @ApiModelProperty(value = "过期时间")
    private String expires_in;

    @ApiModelProperty(value = "手机号")
    private String purePhoneNumber;

    public String getPurePhoneNumber() {
        return purePhoneNumber;
    }

    public void setPurePhoneNumber(String purePhoneNumber) {
        this.purePhoneNumber = purePhoneNumber;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }
}
