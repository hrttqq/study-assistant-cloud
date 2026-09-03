package com.study.common.core.enumConstants;


/**
 * Redis相关的枚举
 *
 * @author 枫澜
 */
public class RedisEnum {
    /**
     * redis的key枚举
     */
    public enum RedisKeyEnum {

        /**
         * 缓存系统所有敏感字信息
         */
        PLATFORM_SENSITIVE_WORD("platform_sensitive_word_", "缓存系统所有敏感字信息"),
        /**
         * 企业邀请码的Redis的前缀
         */
        PLATFORM_BUSINESS_INVITATION_CODE("platform_business_invitation_code_", "企业邀请码的Redis的前缀"),
        /**
         * 平台参数设置参数编码的Redis的前缀
         */
        PLATFORM_PARAM_SET_PARAM_CODE("platform_param_set_param_code_", "平台参数设置参数编码的Redis的前缀"),
        LOGIN_TOKEN_PREFIX("login_token_prefix_", "单点登录Token数据Redis前缀"),
        /**
         * 全局 默认自增KEY
         */
        SYSTEM_DEFAULT_ID_INCR_KEY("system_default_id_incr_key_", "全局 默认自增KEY"),
        /**
         * 物流网络管理redis的KEY前缀
         */
        FPS_NETWORK_KEY("fps_network_key_", "物流网络管理redis的KEY前缀"),
        /**
         * 常见问题点击量
         */
        FPS_QUESTIONS_HITS("fps_questions_hits_", "常见问题点击量");

        /**
         * 编码
         */
        private String code;
        /**
         * 描述
         */
        private String desc;

        /**
         * 获取编码
         *
         * @return 编码
         */
        public String getCode() {
            return this.code;
        }

        /**
         * 获取描述
         *
         * @return 描述
         */
        public String getDesc() {
            return desc;
        }

        RedisKeyEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * redis过期时间枚举
     */
    public enum RedisExpirationTimeEnum {
        /**
         * 登录图形验证码过期时间 2min
         */
        PLATFORM_LOGIN_VERIFY_CODE(60 * 2, "登录图形验证码过期时间 2min"),
        /**
         * 用户信息过期时间 7d
         */
        PLATFORM_USER_INFO(60 * 60 * 24 * 7, "用户信息过期时间 7d"),
        /**
         * 注册图形验证码过期时间 30min
         */
        PLATFORM_REGISTER_GRAPH_VERIFY_CODE(60 * 30, "注册图形验证码过期时间 30min"),
        /**
         * 短信验证码过期时间 1min
         */
        PLATFORM_MESSAGE_VALIDATE_CODE(60 * 5, "短信验证码过期时间 5min"),
        /**
         * 数据字典过期时间 1d
         */
        FPS_DICTIONARY(60 * 60 * 24, "数据字典过期时间 1d"),
        /**
         * redis过期时间 3s
         */
        REDIS_EXPIRATION_TIME_3_S(3, "redis过期时间 3s"),
        ;

        /**
         * 编码
         */
        private int code;
        /**
         * 描述
         */
        private String desc;

        /**
         * 获取编码
         *
         * @return 编码
         */
        public int getCode() {
            return code;
        }

        /**
         * 获取描述
         *
         * @return 描述
         */
        public String getDesc() {
            return desc;
        }

        RedisExpirationTimeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
