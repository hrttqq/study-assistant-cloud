package com.study.common.core.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.util.DateUtils;
import com.mz.hyzs.commons.toolunit.model.UserTokenResponseVO;
import com.study.common.core.constant.StudyConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestClientException;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 锁判断
 *
 * @author gq
 * @date 2022/7/17 16:28
 */
@Slf4j
public class RedisLockUtil {

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 用户授权
     */
    public static final String USER_LOGIN = "user_login";

    /**
     * 小程序新增服务评价
     */
    public static final String ADD_USER_EVALUATE = "add_user_evaluate";


    /**
     * 提送大会员
     */
    public static final String PUSH_BIG_MEMBER = "push_big_member";


    /**
     * 微信订阅常规推送
     */
    public static final String PUSH_WACHAT_SUBSCRIBE_ROUTINE = "push_wechat_subscribe_routine";

    /**
     * 微信订阅变更后推送
     */
    public static final String PUSH_WACHAT_SUBSCRIBE_MODIFY = "push_wechat_subscribe_modify";


    /**
     * 拉取航班
     */
    public static final String PULL_SHIP_ROUTE = "pull_ship_route";


    /**
     * 拉取船泊编码
     */
    public static final String PULL_SHIP_CODE = "pull_ship_code";

    /**
     * 修改内容管理失效自动变更状态
     */
    public static final String UPDATE_EDUNEWSARTICLES_END_DATE = "update_edunewsarticles_end_date";

    /**
     * 菜品计算平均分
     */
    public static final String DISHES_VAG_LEVEL_CALCULATION = "dishes_vag_level_calculation";


    /**
     * 活动日程修改状态-变更为已过期
     */
    public static final String FPS_CS_EVENT_CALENDAR_UPDATE_STATUS = "fps_cs_event_calendar_update_status";


    /**
     * 推送爱心服务订阅
     */
    public static final String PUSH_LOVE_SERVICE_SUBSCRIBE = "push_love_service_subscribe";

    /**
     * 爱心服务取消
     */
    public static final String CANCEL_LOVE_SERVICE = "cancel_love_service";

    /**
     * 爱心服务添加
     */
    public static final String ADD_LOVE_SERVICE = "add_love_service";

    /**
     * 用户地址新增
     */
    public static final String USER_ADDRESS_ADD = "user_address_add";

    /**
     * 用户地址修改
     */
    public static final String USER_ADDRESS_UPDATE = "user_address_update";

    /**
     * 自动生成二维码qrCode
     */
    public static final String WX_INIT_QR_CODE = "wx_init_qr_code";


    /**
     * 活动已完成订阅
     */
    public static final String PUSH_FINISH_ACTIVITY_SUBSCRIBE = "push_finish_activity_subscribe";

    /**
     * 获取港口
     */
    public static final String PULL_PORTS = "pull_ports";

    /**
     * 系统参数
     */
    public static final String FPS_SYS_PARAMS = "fps_sys_params";

    /**
     * 失物申领
     */
    public static final String LOST_APPLY_CLAIM = "lost_apply_claim";


    /**
     * 判断锁
     *
     * @return java.lang.Boolean
     * @author gq
     * @date 2022/7/17 16:30
     * @Param: keys
     * @Param: msg
     * @Param: time
     */
    public static Boolean checkLock(String keys, String msg, int time) {
        Boolean flag = false;
        time = (time <= 0) ? 3 : time;
        String key = SpringContextUtil.getRedisPrefix() + keys + "_" + msg;
        String value = RedisUtil.get(key);
        if (null == value || value.equals("-5")) {
            RedisUtil.set(key, msg, time);
            flag = true;
        }
        return flag;
    }


    /**
     * 尝试获取分布式锁
     *
     * @return 是否获取成功
     * @author gq
     * @date 2022/7/18 20:11
     * @Param: lockKey 锁
     * @Param: requestId 请求标识
     * @Param: expireTime 超期时间
     */
    public static boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {

        Jedis jedis = RedisUtil.getJedis();
        boolean flag = null == jedis ? false : true;
        if (!flag) {
            return false;
        }
        String result = null;
        try {
            result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        } catch (Exception e) {
            log.error("获取Redis失败", e);
            RedisUtil.returnBrokenResource(jedis);
        } finally {
            RedisUtil.returnResource(jedis);
        }
        if (null != result && LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }
   /* public static boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {

        Jedis jedis = RedisUtil.getJedis();
        boolean flag =  null == jedis ? false : true;
        log.info("--------------------------------"+flag);
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }*/

    /**
     * 获取分布式锁并等待
     *
     * @param lockKey     锁
     * @param requestId   请求标识
     * @param expireTime  过期时间,单位秒
     * @param waitTimeout 超时时间,单位毫秒
     * @return 是否获取成功
     */
    public static boolean tryLockWait(String lockKey, String requestId, int expireTime, long waitTimeout) {
        long nanoTime = System.nanoTime(); // 当前时间
        try {
            log.info("开始获取分布式锁-key[{}]", lockKey);
            int count = 0;
            do {
                log.info("尝试获取分布式锁-key[{}]requestId[{}]count[{}]", lockKey, requestId, count);

                boolean result = tryGetDistributedLock(lockKey, requestId, expireTime);

                if (result) {
                    log.info("尝试获取分布式锁-key[{}]成功", lockKey);
                    return true;
                }
                Thread.sleep(100L);//休眠100毫秒
                count++;
            } while ((System.nanoTime() - nanoTime) < TimeUnit.MILLISECONDS.toNanos(waitTimeout));

        } catch (Exception e) {
            log.debug("尝试获取分布式等待锁-key[{}]异常", lockKey);
            log.error(e.getMessage(), e);
        }
        return false;
    }


    /**
     * 获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 过期时间,单位秒
     * @param waitTimeout 超时时间,单位毫秒
     * @return 是否获取成功
     */
  /*  public static boolean tryLock(String lockKey, String requestId, int expireTime, long waitTimeout) {
        long nanoTime = System.nanoTime(); // 当前时间
        try{
            String script = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 then if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end else return 0 end";

            log.info("开始获取分布式锁-key[{}]",lockKey);
            int count = 0;
            do{
                RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);

                log.debug("尝试获取分布式锁-key[{}]requestId[{}]count[{}]",lockKey,requestId,count);
                Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey),requestId,expireTime);

                if(SUCCESS.equals(result)) {
                    log.debug("尝试获取分布式锁-key[{}]成功",lockKey);
                    return true;
                }

                Thread.sleep(500L);//休眠500毫秒
                count++;
            }while ((System.nanoTime() - nanoTime) < TimeUnit.MILLISECONDS.toNanos(waitTimeout));

        }catch(Exception e){
            log.error("尝试获取分布式锁-key[{}]异常",lockKey);
            log.error(e.getMessage(),e);
        }

        return false;
    }*/

    /**
     * 释放分布式锁
     *
     * @return 是否释放成功
     * @author gq
     * @date 2022/7/18 20:08
     * @Param: lockKey 锁
     * @Param: requestId 请求标识
     */
    public static boolean releaseDistributedLock(String lockKey, String requestId) {
        try {
            Jedis jedis = RedisUtil.getJedis();
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            log.error("requestId:" + requestId + "释放锁失败", e);
        }
        return false;
    }

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
            UserTokenResponseVO chatUserInfo = HttpHelpUtil.jsonToObj(restInterface.get(url), UserTokenResponseVO.class);
            log.info("RedisLockUtil_accessToken_chatUserInfo:{},getTime:{}", JSON.toJSONString(chatUserInfo), getTime);
            if (null != chatUserInfo && StringUtils.isNotBlank(chatUserInfo.getAccess_token())) {
                String accessToken = chatUserInfo.getAccess_token();
                JSONObject tokenJson = new JSONObject();
                tokenJson.put("accessToken", accessToken);
                tokenJson.put("getTime", getTime);
                RedisUtil.set(key, JSON.toJSONString(tokenJson), ACCESS_TOKEN_CACHE_TTL);
                log.info("RedisLockUtil_accessToken_accessToken:{},getTime:{}", accessToken, getTime);
                return accessToken;
            }
        } catch (RestClientException e) {
            log.error("调用微信获取accessToken异常", e);
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
                log.info("callWechatApiWithAutoRefresh_获取accessToken失败，token为空");
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
                        log.info("callWechatApiWithAutoRefresh_accessToken失效(errcode=40001)，刷新token并重试");
                        String newAccessToken = refreshAccessToken(appletAppId, appletSecret, restInterface);
                        if (StringUtils.isNotBlank(newAccessToken)) {
                            return apiCall.call(newAccessToken);
                        } else {
                            log.info("callWechatApiWithAutoRefresh_刷新token失败，newAccessToken为空");
                        }
                    }
                } catch (Exception parseEx) {
                    // 结果不是JSON格式，忽略解析异常，直接返回原始结果
                    log.info("callWechatApiWithAutoRefresh_解析结果非JSON格式，跳过40001检测");
                }
            }

            return result;
        } catch (Exception e) {
            log.error("callWechatApiWithAutoRefresh_调用微信API异常", e);
            return "";
        }
    }
}
