package com.study.common.core.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.util.DateUtils;
import com.study.common.core.constant.StudyConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestClientException;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 閿佸垽鏂?
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
     * 鐢ㄦ埛鎺堟潈
     */
    public static final String USER_LOGIN = "user_login";

    /**
     * 灏忕▼搴忔柊澧炴湇鍔¤瘎浠?
     */
    public static final String ADD_USER_EVALUATE = "add_user_evaluate";


    /**
     * 鎻愰€佸ぇ浼氬憳
     */
    public static final String PUSH_BIG_MEMBER = "push_big_member";


    /**
     * 寰俊璁㈤槄甯歌鎺ㄩ€?
     */
    public static final String PUSH_WACHAT_SUBSCRIBE_ROUTINE = "push_wechat_subscribe_routine";

    /**
     * 寰俊璁㈤槄鍙樻洿鍚庢帹閫?
     */
    public static final String PUSH_WACHAT_SUBSCRIBE_MODIFY = "push_wechat_subscribe_modify";


    /**
     * 鎷夊彇鑸彮
     */
    public static final String PULL_SHIP_ROUTE = "pull_ship_route";


    /**
     * 鎷夊彇鑸规硦缂栫爜
     */
    public static final String PULL_SHIP_CODE = "pull_ship_code";

    /**
     * 淇敼鍐呭绠＄悊澶辨晥鑷姩鍙樻洿鐘舵€?
     */
    public static final String UPDATE_EDUNEWSARTICLES_END_DATE = "update_edunewsarticles_end_date";

    /**
     * 鑿滃搧璁＄畻骞冲潎鍒?
     */
    public static final String DISHES_VAG_LEVEL_CALCULATION = "dishes_vag_level_calculation";


    /**
     * 娲诲姩鏃ョ▼淇敼鐘舵€?鍙樻洿涓哄凡杩囨湡
     */
    public static final String FPS_CS_EVENT_CALENDAR_UPDATE_STATUS = "fps_cs_event_calendar_update_status";


    /**
     * 鎺ㄩ€佺埍蹇冩湇鍔¤闃?
     */
    public static final String PUSH_LOVE_SERVICE_SUBSCRIBE = "push_love_service_subscribe";

    /**
     * 鐖卞績鏈嶅姟鍙栨秷
     */
    public static final String CANCEL_LOVE_SERVICE = "cancel_love_service";

    /**
     * 鐖卞績鏈嶅姟娣诲姞
     */
    public static final String ADD_LOVE_SERVICE = "add_love_service";

    /**
     * 鐢ㄦ埛鍦板潃鏂板
     */
    public static final String USER_ADDRESS_ADD = "user_address_add";

    /**
     * 鐢ㄦ埛鍦板潃淇敼
     */
    public static final String USER_ADDRESS_UPDATE = "user_address_update";

    /**
     * 鑷姩鐢熸垚浜岀淮鐮乹rCode
     */
    public static final String WX_INIT_QR_CODE = "wx_init_qr_code";


    /**
     * 娲诲姩宸插畬鎴愯闃?
     */
    public static final String PUSH_FINISH_ACTIVITY_SUBSCRIBE = "push_finish_activity_subscribe";

    /**
     * 鑾峰彇娓彛
     */
    public static final String PULL_PORTS = "pull_ports";

    /**
     * 绯荤粺鍙傛暟
     */
    public static final String FPS_SYS_PARAMS = "fps_sys_params";

    /**
     * 澶辩墿鐢抽
     */
    public static final String LOST_APPLY_CLAIM = "lost_apply_claim";


    /**
     * 鍒ゆ柇閿?
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
     * 灏濊瘯鑾峰彇鍒嗗竷寮忛攣
     *
     * @return 鏄惁鑾峰彇鎴愬姛
     * @author gq
     * @date 2022/7/18 20:11
     * @Param: lockKey 閿?
     * @Param: requestId 璇锋眰鏍囪瘑
     * @Param: expireTime 瓒呮湡鏃堕棿
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
            log.error("鑾峰彇Redis澶辫触", e);
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
     * 鑾峰彇鍒嗗竷寮忛攣骞剁瓑寰?
     *
     * @param lockKey     閿?
     * @param requestId   璇锋眰鏍囪瘑
     * @param expireTime  杩囨湡鏃堕棿,鍗曚綅绉?
     * @param waitTimeout 瓒呮椂鏃堕棿,鍗曚綅姣
     * @return 鏄惁鑾峰彇鎴愬姛
     */
    public static boolean tryLockWait(String lockKey, String requestId, int expireTime, long waitTimeout) {
        long nanoTime = System.nanoTime(); // 褰撳墠鏃堕棿
        try {
            log.info("寮€濮嬭幏鍙栧垎甯冨紡閿?key[{}]", lockKey);
            int count = 0;
            do {
                log.info("灏濊瘯鑾峰彇鍒嗗竷寮忛攣-key[{}]requestId[{}]count[{}]", lockKey, requestId, count);

                boolean result = tryGetDistributedLock(lockKey, requestId, expireTime);

                if (result) {
                    log.info("灏濊瘯鑾峰彇鍒嗗竷寮忛攣-key[{}]鎴愬姛", lockKey);
                    return true;
                }
                Thread.sleep(100L);//浼戠湢100姣
                count++;
            } while ((System.nanoTime() - nanoTime) < TimeUnit.MILLISECONDS.toNanos(waitTimeout));

        } catch (Exception e) {
            log.debug("灏濊瘯鑾峰彇鍒嗗竷寮忕瓑寰呴攣-key[{}]寮傚父", lockKey);
            log.error(e.getMessage(), e);
        }
        return false;
    }


    /**
     * 鑾峰彇鍒嗗竷寮忛攣
     * @param lockKey 閿?
     * @param requestId 璇锋眰鏍囪瘑
     * @param expireTime 杩囨湡鏃堕棿,鍗曚綅绉?
     * @param waitTimeout 瓒呮椂鏃堕棿,鍗曚綅姣
     * @return 鏄惁鑾峰彇鎴愬姛
     */
  /*  public static boolean tryLock(String lockKey, String requestId, int expireTime, long waitTimeout) {
        long nanoTime = System.nanoTime(); // 褰撳墠鏃堕棿
        try{
            String script = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 then if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end else return 0 end";

            log.info("寮€濮嬭幏鍙栧垎甯冨紡閿?key[{}]",lockKey);
            int count = 0;
            do{
                RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);

                log.debug("灏濊瘯鑾峰彇鍒嗗竷寮忛攣-key[{}]requestId[{}]count[{}]",lockKey,requestId,count);
                Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey),requestId,expireTime);

                if(SUCCESS.equals(result)) {
                    log.debug("灏濊瘯鑾峰彇鍒嗗竷寮忛攣-key[{}]鎴愬姛",lockKey);
                    return true;
                }

                Thread.sleep(500L);//浼戠湢500姣
                count++;
            }while ((System.nanoTime() - nanoTime) < TimeUnit.MILLISECONDS.toNanos(waitTimeout));

        }catch(Exception e){
            log.error("灏濊瘯鑾峰彇鍒嗗竷寮忛攣-key[{}]寮傚父",lockKey);
            log.error(e.getMessage(),e);
        }

        return false;
    }*/

    /**
     * 閲婃斁鍒嗗竷寮忛攣
     *
     * @return 鏄惁閲婃斁鎴愬姛
     * @author gq
     * @date 2022/7/18 20:08
     * @Param: lockKey 閿?
     * @Param: requestId 璇锋眰鏍囪瘑
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
            log.error("releaseDistributedLock failed, requestId:{}", requestId, e);
        }
        return false;
    }


}
