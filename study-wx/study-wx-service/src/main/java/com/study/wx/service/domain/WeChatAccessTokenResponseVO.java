package com.study.wx.service.domain;


/**
 * 微信获取access token的响应.
 * Date 2018/12/26
 */

public class WeChatAccessTokenResponseVO {

    private String access_token;//接口调用凭证

    private String session_key;//会话密钥，微信小程序登陆独有

    private String expires_in;//access_token接口调用凭证超时时间，单位（秒）

    private String refresh_token;//用户刷新access_token

    private String openid;//授权用户唯一标识

    private String scope;//用户授权的作用域，使用逗号（,）分隔

    private String unionId;//当且仅当该移动应用已获得该用户的userinfo授权时，才会出现该字段

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}
