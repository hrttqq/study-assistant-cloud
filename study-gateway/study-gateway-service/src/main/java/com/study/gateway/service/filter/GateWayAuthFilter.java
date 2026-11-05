package com.study.gateway.service.filter;

import com.alibaba.fastjson.JSON;
import com.study.common.core.enumConstants.ErrorCodeEnum;
import com.study.common.core.enumConstants.RedisEnum;
import com.study.common.core.model.ResultMode;
import com.study.common.core.model.TokenInfo;
import com.study.common.core.utils.JwtUtil;
import com.study.common.core.utils.RedisUtil;
import com.study.common.core.utils.SpringContextUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 网关拦截器
 */
@Component
@Slf4j
public class GateWayAuthFilter implements GlobalFilter, Ordered {


    public static final String USER_ID = "X-Study-User-Id";
    public static final String LOGIN_ID = "X-Study-Login-Id";
    public static final String WX_USER_ID = "X-Study-Wx-User-Id";
    public static final String TOKEN_ID = "X-Study-Token-Id";

    @Value("${auth.skip.urls}")
    private String[] ignoreUri;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        // 始终清理客户端伪造的内部身份请求头
        ServerWebExchange sanitized = exchange.mutate()
                .request(builder -> builder.headers(headers -> {
                    headers.remove(USER_ID);
                    headers.remove(LOGIN_ID);
                    headers.remove(WX_USER_ID);
                    headers.remove(TOKEN_ID);
                }))
                .build();

        log.info("==========开始鉴权=============");
        ServerHttpRequest request = sanitized.getRequest();
        ServerHttpResponse response = sanitized.getResponse();
        //获取请求path
        String path = sanitized.getRequest()
                .getURI()
                .getPath();
        log.info("==========获取请求路径:{}", path);
        log.info("白名单请求path过滤");
        for (String s : ignoreUri) {
            if (path.contains(s)) {
                log.info("==========path:{}匹配gateway白名单路径，无需鉴权", path);
                //直接通过，传输到下一级
                return chain.filter(exchange);
            }
        }

        HttpMethod method = exchange.getRequest().getMethod();
        //获取请求的token值
        String authorization = request.getHeaders().getFirst(JwtUtil.getInstance().getTokenHeader());
        log.info("==========获取当前登录token:{}", authorization);
        String token = extractBearerToken(authorization);
        ResultMode<String> resultMode = checkAuth(path, token);
        if (resultMode.getSucceed()) {
            log.info("================鉴权结束");
            log.info("================checkAuth success===================");
            //将登录信息保存到下一级
            ServerHttpRequest newRequest = request.mutate().header(JwtUtil.getInstance().getTokenHeader(), token).build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
            return chain.filter(newExchange);
        } else {
            //都设置返回状态码为最接受
            return buildReqsponse(response, HttpStatus.ACCEPTED, resultMode);
        }
    }

    private ResultMode<String> checkAuth(String path, String token) {
        ResultMode<String> resultMode = new ResultMode<>();
        // 如果未携带token
        if (StringUtils.isBlank(token)) {
            //返回登录令牌为空错误
            resultMode.setErrCode(ErrorCodeEnum.NOTOKEN.getCode());
            resultMode.setErrMsg(ErrorCodeEnum.NOTOKEN.getDesc());
            resultMode.setSucceed(false);
            return resultMode;
        }
        // 获取签名信息
        Claims claims = JwtUtil.getInstance().parseClaims(token);
        log.info("===========签名信息claims:{}", claims);

        // 判断签名是否存在或过期
        boolean b = claims == null || claims.isEmpty() || JwtUtil.getInstance().isTokenExpired(claims.getExpiration());
        if (b) {
            //返回登录凭证过期
            resultMode.setErrCode(ErrorCodeEnum.CODE_100004.getCode());
            resultMode.setErrMsg(ErrorCodeEnum.CODE_100004.getDesc());
            resultMode.setSucceed(false);
            return resultMode;
        }

        //从当前token中得到用户id
        String subject = claims.getSubject();
        // 判断当前用户是否在另一个客户端登陆了，这里目的是保证一个用户只能在一个客户端上登录
        TokenInfo tokenInfo = JSON.parseObject(subject, TokenInfo.class);

        String userId = tokenInfo.getUserId();
        //从redis中获取登录时存入的token
        String redisKeyToken = SpringContextUtil.getRedisPrefix() + RedisEnum.RedisKeyEnum.LOGIN_TOKEN_PREFIX.getCode() + userId;
        String oldToken = RedisUtil.get(redisKeyToken);
        log.info("==========获取登录时redis中存的token:{}", oldToken);
        if (!StringUtils.equals(token, oldToken)) {
            resultMode.setErrCode(ErrorCodeEnum.CODE_100007.getCode());
            resultMode.setErrMsg(ErrorCodeEnum.CODE_100007.getDesc());
            resultMode.setSucceed(false);
            return resultMode;
        }
        log.info("================匹配url开始");
        if (!analysisPathUrl(path, claims)) {
            //返回未授权错误
            resultMode.setErrCode(ErrorCodeEnum.CODE_401.getCode());
            resultMode.setErrMsg(ErrorCodeEnum.CODE_401.getDesc());
            resultMode.setSucceed(false);
            return resultMode;
        }
        resultMode.setSucceed(true);
        return resultMode;
    }


    /**
     * TODO用户权限校验，后续实现
     *
     * @param path
     * @param claims
     * @return
     */
    private boolean analysisPathUrl(String path, Claims claims) {
        return true;
    }

    /**
     * token值校验
     * 只接受 Authorization: Bearer <token> 格式。
     */
    private String extractBearerToken(String authorization) {

        if (!hasText(authorization)) {
            return null;
        }

        String value = authorization.trim();

        if (!value.regionMatches(
                true, 0, "Bearer ", 0, 7)) {
            return null;
        }

        String token = value.substring(7).trim();

        return token.isEmpty() ? null : token;
    }

    /**
     * 自定义返回结果
     *
     * @param response   响应
     * @param httpStatus http状态
     * @param resultMode 返回实体
     * @return Mono<Void>
     */
    public static Mono<Void> buildReqsponse(ServerHttpResponse response, HttpStatus httpStatus, ResultMode<String> resultMode) {
        response.setStatusCode(httpStatus);
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(JSON.toJSONString(resultMode).getBytes());
        return response.writeWith(Mono.just(bodyDataBuffer));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    @Override
    public int getOrder() {
        // 在路由转发前执行认证
        return -100;
    }
}
