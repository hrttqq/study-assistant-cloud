package com.study.common.core.enumconstants;

/**
 * 系统异常代码枚举
 *
 * @author 枫澜
 */
public enum ErrorCodeEnum {
    /**
     * token为空
     */
    NOTOKEN("000000", "token为空"),
    /**
     * 未登录
     */
    NOLOGIN("100001", "未登录"),
    /**
     * 登录用户不存在
     */
    NO_LOGIN_USER("000001", "登录用户不存在"),
    /**
     * 用户不存在
     */
    NO_USER("000002", "用户不存在"),
    /**
     * 找不到方法
     */
    CODE_006("006", "找不到方法！"),
    /**
     * 无权使用
     */
    NOAUTH("100002", "无权使用"),
    /**
     * 参数校验失败
     */
    PARAM_CHECK("100003", "参数校验失败"),
    /**
     * 登录凭证过期
     */
    CODE_100004("100004", "登录凭证过期！"),
    /**
     * 数据库操作异常
     */
    CODE_100005("100005", "数据库操作异常!"),
    /**
     * 验证码为空
     */
    LOGIN_VERIFY_CODE("100006", "验证码为空"),
    /**
     * 登录异常!当前账号已在其他地方登录
     */
    CODE_100007("100007", "登录异常!当前账号已在其他地方登录!"),
    /**
     * 验证码过期
     */
    LOGIN_VERIFY_CODE_EXPIRED("100008", "验证码过期"),
    /**
     * 不可重复发送
     */
    LOGIN_VERIFY_CODE_REPEAT("100009", "不可重复发送"),

    /**
     * 小程序未授权
     */
    CODE_100010("100010", "小程序未授权"),
    /**
     * 验证码过期
     */
    LOGIN_VERIFY_CODE_ERROR("1000011", "验证码错误"),

    /**
     * 数据重复
     */
    DUPLICATED_DATA("1000013", "数据重复"),
    /**
     * 活动已过期
     */
    //ACTIVITY_EXPIRED("1000012", "活动已过期"),

    //ACTIVITY_ADDRESS_USING("1000014","存在使用该地址的活动"),

    GET_WX_USER_INFO_NULL("1000015", "获取用户信息不存在"),

    USER_FROZEN("1000016", "用户已冻结"),
    REQUEST_TOO_OFTEN("1000017", "操作过于频繁"),

    /**
    /**
     * 操作失败
     */
    CODE_MINUSONE("-1", "操作失败!"),
    /**
     * 操作成功
     */
    CODE_200("200", "操作成功！！"),
    /**
     * 未授权
     */
    CODE_401("401", "未授权"),
    /**
     * 请求错误
     */
    CODE_400("400", "请求错误"),
    /**
     * 服务器拒绝请求
     */
    CODE_403("403", "服务器拒绝请求"),
    /**
     * 找不到路径
     */
    CODE_404("404", "服务器找不到路径信息"),
    /**
     * 方法禁用
     */
    CODE_405("405", "方法禁用"),
    /**
     * 冲突
     */
    CODE_409("409", "冲突"),
    /**
     * 服务器内部错误
     */
    CODE_500("500", "服务器内部错误"),

    DISHES_REPEAT("1000018", "名称请勿重复"),

    WINDOW_REPEAT("1000019", "弹窗/浮窗重复"),


    /**
     *手机号码不存在
     */
    NOPHONE("1000021", "手机号码为空"),

    DATA_NO_EAIST("1000024", "数据不存在")
    ;

    /**
     * 成员描述
     */
    private final String desc;
    /**
     * 索引编码
     */
    private final String code;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    ErrorCodeEnum(String code, String desc) {
        this.desc = desc;
        this.code = code;
    }
}
