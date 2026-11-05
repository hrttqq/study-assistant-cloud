
package com.study.common.core.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户信息
 *
 * 供各业务微服务统一获取当前请求的用户身份。
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统用户ID
     */
    private String userId;

    /**
     * 登录账号ID
     */
    private String loginId;

    /**
     * 微信用户ID
     */
    private String wxUserId;

    /**
     * Token唯一标识
     */
    private String tokenId;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 登录方式
     */
    private String loginType;

    /**
     * 客户端类型
     */
    private String clientType;

    /**
     * 角色编码
     */
    private List<String> roleCodeList;

    /**
     * 权限编码
     */
    private List<String> permissionCodeList;

    /**
     * 是否超级管理员
     */
    private Boolean superAdmin;
}
