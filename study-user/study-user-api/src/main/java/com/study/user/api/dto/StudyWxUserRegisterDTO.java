package com.study.user.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudyWxUserRegisterDTO implements Serializable {
    /**
     * 建议序列化的class都给一个序列化的ID，这样可以保证序列化的成功，版本的兼容性。
     */
    private static final long serialVersionUID = 1L;


    /**
     * 小程序 appId
     */
    private String appId;

    /**
     * 微信 openId
     */
    private String openId;

    /**
     * 微信 unionId
     */
    private String unionId;

    /**
     * 微信 sessionKey
     */
    private String sessionKey;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private String gender;
}
