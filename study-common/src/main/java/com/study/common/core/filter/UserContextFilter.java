
package com.study.common.core.filter;

import com.study.common.core.context.UserContext;
import com.study.common.core.model.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 用户上下文过滤器
 *
 * 将 Gateway 认证后传递的身份信息转换为 LoginUser，
 * 并绑定到当前请求线程。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class UserContextFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID =
            "X-Study-User-Id";

    private static final String HEADER_LOGIN_ID =
            "X-Study-Login-Id";

    private static final String HEADER_WX_USER_ID =
            "X-Study-Wx-User-Id";

    private static final String HEADER_TOKEN_ID =
            "X-Study-Token-Id";

    private static final String HEADER_USERNAME =
            "X-Username";

    private static final String HEADER_NICKNAME =
            "X-Nickname";

    private static final String HEADER_LOGIN_TYPE =
            "X-Login-Type";

    private static final String HEADER_CLIENT_TYPE =
            "X-Client-Type";

    private static final String HEADER_ROLE_CODES =
            "X-Role-Codes";

    private static final String HEADER_PERMISSION_CODES =
            "X-Permission-Codes";

    private static final String HEADER_SUPER_ADMIN =
            "X-Super-Admin";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            LoginUser loginUser = buildLoginUser(request);

            if (loginUser != null) {
                UserContext.setCurrentUser(loginUser);
            }

            filterChain.doFilter(request, response);

        } finally {
            // 必须清理，防止 Tomcat 线程复用造成身份串号
            UserContext.clear();
        }
    }

    /**
     * 将请求中的可信身份信息转换为 LoginUser。
     */
    private LoginUser buildLoginUser(HttpServletRequest request) {

        String userId = request.getHeader(HEADER_USER_ID);

        // 没有登录身份时，不建立用户上下文
        if (!hasText(userId)) {
            return null;
        }

        LoginUser loginUser = new LoginUser();

        loginUser.setUserId(userId);
        loginUser.setLoginId(
                request.getHeader(HEADER_LOGIN_ID));
        loginUser.setWxUserId(
                request.getHeader(HEADER_WX_USER_ID));
        loginUser.setTokenId(
                request.getHeader(HEADER_TOKEN_ID));
        loginUser.setUsername(
                request.getHeader(HEADER_USERNAME));
        loginUser.setNickname(
                request.getHeader(HEADER_NICKNAME));
        loginUser.setLoginType(
                request.getHeader(HEADER_LOGIN_TYPE));
        loginUser.setClientType(
                request.getHeader(HEADER_CLIENT_TYPE));

        loginUser.setRoleCodeList(
                parseList(request.getHeader(HEADER_ROLE_CODES)));

        loginUser.setPermissionCodeList(
                parseList(request.getHeader(HEADER_PERMISSION_CODES)));

        String superAdmin = request.getHeader(HEADER_SUPER_ADMIN);

        loginUser.setSuperAdmin(
                "true".equalsIgnoreCase(superAdmin));

        return loginUser;
    }

    private List<String> parseList(String value) {

        if (!hasText(value)) {
            return Collections.emptyList();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(this::hasText)
                .toList();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
