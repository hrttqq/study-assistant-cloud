package com.study.common.core.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TokenInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Token唯一标识，用于区分每一次登录生成的Token
     */
    private String tokenId;

    /**
     * 系统用户ID
     */
    private String userId;

    /**
     * 登录账号ID，可用于区分用户主体和登录账号
     */
    private String loginId;

    /**
     * 用户名，通常为系统登录账号
     */
    private String username;

    /**
     * 用户昵称，用于展示
     */
    private String nickname;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 登录方式，例如：WX_MINI、PASSWORD、SMS、ADMIN
     */
    private String loginType;

    /**
     * 客户端类型，例如：MINI_APP、WEB、APP、ADMIN
     */
    private String clientType;

    /**
     * 微信小程序appId，微信登录场景下使用
     */
    private String appId;

    /**
     * 微信用户表ID
     */
    private String wxUserId;

    /**
     * 微信openId，微信用户在当前小程序下的唯一标识
     */
    private String openId;

    /**
     * 当前用户拥有的角色编码集合
     */
    private List<String> roleCodeList;

    /**
     * 当前用户拥有的权限编码集合
     */
    private List<String> permissionCodeList;

    /**
     * 是否超级管理员
     */
    private Boolean superAdmin;

    /**
     * 会员等级，例如：FREE、VIP、SVIP
     */
    private String memberLevel;

    /**
     * 会员过期时间
     */
    private LocalDateTime memberExpireAt;

    /**
     * Token签发时间，时间戳，单位毫秒
     */
    private Long issuedAt;

    /**
     * Token过期时间，时间戳，单位毫秒
     */
    private Long expireAt;

    /**
     * RefreshToken过期时间，时间戳，单位毫秒
     */
    private Long refreshExpireAt;

    /**
     * Token有效时长，单位秒
     */
    private Integer expiresIn;

    /**
     * 当前登录设备ID
     */
    private String deviceId;

    /**
     * 当前登录IP地址
     */
    private String ip;

    /**
     * 当前登录客户端User-Agent
     */
    private String userAgent;

    /**
     * 登录版本号，用于强制下线或Token失效控制
     */
    private String loginVersion;

    /**
     * 用户状态，例如：NORMAL、DISABLED、DELETED
     */
    private String status;

    /**
     * 当前登录会话ID
     */
    private String sessionId;

    /**
     * 是否开启单点登录控制
     */
    private Boolean singleLogin;

}
