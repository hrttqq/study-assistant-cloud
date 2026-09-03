package com.study.common.core.utils;

import com.alibaba.fastjson.JSON;
import com.study.common.core.model.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * jwt工具类
 *
 * @author 李建华
 */
@Slf4j
public class JwtUtil {

    private static JwtUtil instance = null;

    private JwtUtil() {

    }

    public static JwtUtil getInstance() {
        if (instance == null) {
            instance = new JwtUtil();
        }
        return instance;
    }

    /**
     * 用户凭证  凭证(校验的变量名)
     */
    private String tokenHeader;

    /**
     * 登录地址,授权地址
     */
    private String loginPath;

    /**
     * 加密key 加密秘钥
     */
    private String secret;

    /**
     * 过期时间
     */
    private int expiresIn;

    public String getTokenHeader() {
        return tokenHeader;
    }

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }

    public String getLoginPath() {
        return loginPath;
    }

    public void setLoginPath(String loginPath) {
        this.loginPath = loginPath;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void init(ConfigurableApplicationContext ctx) {

        /**
         * 用户凭证  凭证(校验的变量名)
         */
        tokenHeader = ctx.getEnvironment().getProperty("jwt.header");

        /**
         * 登录地址,授权地址
         */
        loginPath = ctx.getEnvironment().getProperty("jwt.auth.path");

        /**
         * 加密key 加密秘钥
         */
        secret = ctx.getEnvironment().getProperty("jwt.secret");
        /**
         * 过期时间
         */
        expiresIn = Integer.valueOf(ctx.getEnvironment().getProperty("jwt.expiration") != null ? ctx.getEnvironment().getProperty("jwt.expiration") : "0");
    }

    /**
     * 判断Token是否过期
     *
     * @param expiration 过期日期
     * @return true 过期
     * @author geYang
     * 创建时间2018-05-18 16:41
     */
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }

    /**
     * 生成Token签名
     *
     * @param tokenInfoString tokenInfo对象信息
     * @return 签名字符串
     * @author geYang
     * 创建时间 2018-05-18 16:35
     */
    public String generateToken(String tokenInfoString) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(tokenInfoString)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60L * 1000 * expiresIn))
                .signWith(SignatureAlgorithm.HS256, JwtUtil.getInstance().getSecret())
                .compact();

        // 注意: JDK版本高于1.8, 缺少 javax.xml.bind.DatatypeConverter jar包,编译出错
    }

}
