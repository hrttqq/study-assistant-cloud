package com.study.common.core.enumConstants;

public class DicEnum {

    /**
     * 微信用户状态枚举类
     */
    public enum WxUserStatusEnum {

        DEFAULT("10", "启用"),

        DELETE("20", "停用");

        private final String code;

        private final String desc;

        WxUserStatusEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }


        public static WxUserStatusEnum valueOfByCode(String code) {
            for (WxUserStatusEnum statusEnum : WxUserStatusEnum.values()) {
                if (null != code && statusEnum.code.equals(code)) {
                    return statusEnum;
                }
            }
            return null;
        }
    }
}
