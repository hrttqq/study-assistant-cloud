package com.study.common.core.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT tool.
 */
@Slf4j
public class JwtUtil {

    private static JwtUtil instance = null;

    private String tokenHeader;

    private String loginPath;

    private String secret;

    private int expiresIn;

    private Key signingKey;

    private JwtUtil() {

    }

    public static JwtUtil getInstance() {
        if (instance == null) {
            instance = new JwtUtil();
        }
        return instance;
    }

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
        this.signingKey = null;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void init(ConfigurableApplicationContext ctx) {
        tokenHeader = ctx.getEnvironment().getProperty("jwt.header");
        loginPath = ctx.getEnvironment().getProperty("jwt.auth.path");
        secret = ctx.getEnvironment().getProperty("jwt.secret");
        signingKey = buildSigningKey(secret);
        expiresIn = Integer.parseInt(ctx.getEnvironment().getProperty("jwt.expiration", "0"));
    }

    public boolean isTokenExpired(Date expiration) {
        return expiration != null && expiration.before(new Date());
    }

    public String generateToken(String tokenInfoString) {
        if (StringUtils.isBlank(tokenInfoString)) {
            throw new IllegalArgumentException("tokenInfoString cannot be null or empty");
        }
        if (expiresIn <= 0) {
            throw new IllegalArgumentException("jwt.expiration must be greater than 0");
        }

        Date now = new Date();
        Date expiration = new Date(now.getTime() + 60L * 1000 * expiresIn);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(tokenInfoString)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("token cannot be null or empty");
        }
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        try {
            Claims claims = parseClaims(token);
            return !isTokenExpired(claims.getExpiration());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT token validate failed", e);
            return false;
        }
    }

    private Key getSigningKey() {
        if (signingKey == null) {
            signingKey = buildSigningKey(secret);
        }
        return signingKey;
    }

    private Key buildSigningKey(String secret) {
        if (StringUtils.isBlank(secret)) {
            throw new IllegalArgumentException("jwt.secret cannot be null or empty");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
