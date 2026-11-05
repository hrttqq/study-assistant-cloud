
package com.study.common.core.context;

import com.study.common.core.model.LoginUser;

/**
 * 当前请求用户上下文
 *
 * 基于 ThreadLocal 保存当前请求的登录用户。
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER =
            new ThreadLocal<>();

    private UserContext() {
    }

    /**
     * 设置当前用户
     */
    public static void setCurrentUser(LoginUser loginUser) {
        HOLDER.set(loginUser);
    }

    /**
     * 获取当前用户
     */
    public static LoginUser getCurrentUser() {
        return HOLDER.get();
    }

    /**
     * 获取当前用户ID
     */
    public static String getUserId() {
        LoginUser loginUser = HOLDER.get();

        return loginUser == null
                ? null
                : loginUser.getUserId();
    }

    /**
     * 获取当前微信用户ID
     */
    public static String getWxUserId() {
        LoginUser loginUser = HOLDER.get();

        return loginUser == null
                ? null
                : loginUser.getWxUserId();
    }

    /**
     * 清理当前请求用户
     */
    public static void clear() {
        HOLDER.remove();
    }
}
