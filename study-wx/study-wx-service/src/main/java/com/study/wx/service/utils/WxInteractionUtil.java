package com.study.wx.service.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.util.DateUtils;
import com.study.common.core.constant.StudyConstant;
import com.study.common.core.utils.RedisLockUtil;
import com.study.common.core.utils.RedisUtil;
import com.study.common.core.utils.RestInterface;
import com.study.common.core.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
public class WxInteractionUtil {

    /**
     * accessToken缓存TTL（秒），与Redis缓存过期时间保持一致
     */
    private static final int ACCESS_TOKEN_CACHE_TTL = 7000;

    /**
     * 获取微信AccessToken
     *
     * @return java.lang.String
     * @author gq
     * @date 2022/7/19 16:38
     * @Param: appletAppId
     * @Param: appletSecret
     * @Param: restInterface
     */
    public static String getAccessToken(String appletAppId, String appletSecret, RestInterface restInterface) {

        String key = SpringContextUtil.getRedisPrefix() + StudyConstant.USER_LOGIN_WECHAT + "_token";
        String tokenStr = RedisUtil.get(key);
        JSONObject tokenJson = JSONObject.parseObject(tokenStr);
        if (Objects.nonNull(tokenJson)) {
            Object accessToken = tokenJson.get("accessToken");
            Object getTime = tokenJson.get("getTime");
            log.info("RedisLockUtil_getAccessToken_accessToken:{},getTime:{}", accessToken, getTime);
            if (null != accessToken && !accessToken.equals("-5")) {
                return (String) accessToken;
            } else {
                return getAccessToken(appletAppId, appletSecret, restInterface, key);
            }
        } else {
            return getAccessToken(appletAppId, appletSecret, restInterface, key);
        }
    }

    private static String getAccessToken(String appletAppId, String appletSecret, RestInterface restInterface, String key) {
        String lockKey = SpringContextUtil.getRedisPrefix() + StudyConstant.ACCESS_TOKEN_KEY;
        boolean flag = RedisLockUtil.tryGetDistributedLock(lockKey, "getAccessToken", 3000);
        log.info("RedisLockUtil_getAccessToken_flag:{}", flag);
        if (flag) {
            try {
                return accessToken(key, appletAppId, appletSecret, restInterface);
            } finally {
                // 释放分布式锁，避免其他请求被阻塞
                RedisLockUtil.releaseDistributedLock(lockKey, "getAccessToken");
            }
        }
        return null;
    }

    /**
     * 强制刷新微信AccessToken
     * 直接从微信API获取新token并覆盖Redis缓存中的旧值
     * 适用于当微信接口返回errcode=40001（token失效）时主动刷新
     *
     * @return java.lang.String 新的accessToken
     * @author gq
     * @Param: appletAppId
     * @Param: appletSecret
     * @Param: restInterface
     */
    public static String refreshAccessToken(String appletAppId, String appletSecret, RestInterface restInterface) {
        String key = SpringContextUtil.getRedisPrefix() + StudyConstant.USER_LOGIN_WECHAT + "_token";
        log.info("RedisLockUtil_refreshAccessToken_开始强制刷新accessToken, key:{}", key);
        // 获取分布式锁，直接调用微信API获取新token并覆盖旧缓存
        String lockKey = SpringContextUtil.getRedisPrefix() + StudyConstant.ACCESS_TOKEN_KEY;
        boolean flag = RedisLockUtil.tryGetDistributedLock(lockKey, "getAccessToken", 3000);
        log.info("RedisLockUtil_refreshAccessToken_flag:{}", flag);
        if (flag) {
            try {
                String newToken = accessToken(key, appletAppId, appletSecret, restInterface);
                log.info("RedisLockUtil_refreshAccessToken_刷新accessToken完成, newToken:{}", newToken);
                return newToken;
            } finally {
                // 释放分布式锁，避免其他请求被阻塞
                RedisLockUtil.releaseDistributedLock(lockKey, "getAccessToken");
            }
        }
        log.info("RedisLockUtil_refreshAccessToken_获取分布式锁失败");
        return null;
    }

    /**
     * 调用微信获取accessToken
     *
     * @return java.lang.String
     * @author gq
     * @date 2022/11/2 14:31
     * @Param: key
     * @Param: appletAppId
     * @Param: appletSecret
     * @Param: restInterface
     */
    private static String accessToken(String key, String appletAppId, String appletSecret, RestInterface restInterface) {
        log.info("RedisLockUtil_accessToken_key:{},appletAppId:{}, appletSecret:{}", key, appletAppId, appletSecret);
        String url = String.format(StudyConstant.URL_ACCESS_TOKEN_CLIENT_CREDENTIAL, appletAppId, appletSecret);
        try {
            Date date = new Date();
            String getTime = DateUtils.format(date);
            JSONObject chatUserInfo = JSON.parseObject(restInterface.get(url));
            log.info("RedisLockUtil_accessToken_chatUserInfo:{},getTime:{}", JSON.toJSONString(chatUserInfo), getTime);
            String accessToken = chatUserInfo == null ? null : chatUserInfo.getString("access_token");
            if (StringUtils.isNotBlank(accessToken)) {
                JSONObject tokenJson = new JSONObject();
                tokenJson.put("accessToken", accessToken);
                tokenJson.put("getTime", getTime);
                RedisUtil.set(key, JSON.toJSONString(tokenJson), ACCESS_TOKEN_CACHE_TTL);
                log.info("RedisLockUtil_accessToken_accessToken:{},getTime:{}", accessToken, getTime);
                return accessToken;
            }
        } catch (RestClientException e) {
            log.error("get wechat accessToken failed", e);
        }
        return null;
    }

    /**
     * 微信API调用函数式接口
     * 接收accessToken作为参数，返回微信API调用的结果字符串
     */
    @FunctionalInterface
    public interface WechatApiCall {
        String call(String accessToken) throws Exception;
    }

    /**
     * 带自动刷新机制的微信API调用封装
     * 流程：获取accessToken → 调用微信API → 检测errcode=40001 → 自动刷新token → 重试一次
     *
     * @param appletAppId   小程序appId
     * @param appletSecret  小程序secret
     * @param restInterface REST接口
     * @param apiCall       微信API调用函数，接收accessToken，返回响应结果字符串
     * @return 微信API调用的最终结果字符串，如果获取token失败则返回空字符串
     */
    public static String callWechatApiWithAutoRefresh(String appletAppId, String appletSecret, RestInterface restInterface, WechatApiCall apiCall) {
        try {
            // 1. 获取accessToken
            String accessToken = getAccessToken(appletAppId, appletSecret, restInterface);
            if (StringUtils.isBlank(accessToken)) {
                log.info("callWechatApiWithAutoRefresh accessToken is blank");
                return "";
            }

            // 2. 调用微信API
            String result = apiCall.call(accessToken);

            // 3. 检测errcode=40001（token失效），自动刷新并重试一次
            if (StringUtils.isNotBlank(result)) {
                try {
                    JSONObject resultJson = JSONObject.parseObject(result);
                    String errcode = resultJson.getString("errcode");
                    if ("40001".equals(errcode)) {
                        log.info("callWechatApiWithAutoRefresh accessToken expired, errcode=40001");
                        String newAccessToken = refreshAccessToken(appletAppId, appletSecret, restInterface);
                        if (StringUtils.isNotBlank(newAccessToken)) {
                            return apiCall.call(newAccessToken);
                        } else {
                            log.info("callWechatApiWithAutoRefresh refresh token failed");
                        }
                    }
                } catch (Exception parseEx) {
                    // 结果不是JSON格式，忽略解析异常，直接返回原始结果
                    log.info("callWechatApiWithAutoRefresh response is not valid json");
                }
            }

            return result;
        } catch (Exception e) {
            log.error("callWechatApiWithAutoRefresh failed", e);
            return "";
        }
    }
}
