package com.study.common.core.constant;

/**
 * 学习项目常量类
 */
public class StudyConstant {

    public static final String USER_LOGIN_WECHAT = "user_login_weChat";

    public static final String ACCESS_TOKEN_KEY = "access_token";

    /**
     * 获取client_credential的access_token(微信小程序token)
     * 需要填2个值appid secret
     */
    public static final String URL_ACCESS_TOKEN_CLIENT_CREDENTIAL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    public static final String URL_SUBSCRIBE_SEND = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=%s";
}
