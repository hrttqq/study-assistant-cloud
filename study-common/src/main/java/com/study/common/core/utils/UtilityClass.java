package com.study.common.core.utils;

import com.study.common.core.enumConstants.RedisEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 所有公共单元，可以在这里一起找到
 */
@Slf4j
public class UtilityClass {

    private UtilityClass() {
    }

    private static Random rand;

    static {
        try {
            rand = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException ex) {
            log.error("instantiate Random object error", ex);
        }
    }

    //===========================序列与反序列 start===========

    /**
     * 序列化
     *
     * @param value 对象
     * @return byte[] 二进制数据
     */
    public static byte[] serialize(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv = null;
        try(
           ByteArrayOutputStream bos = new ByteArrayOutputStream();
           ObjectOutputStream os = new ObjectOutputStream(bos);
        ) {
            os.writeObject(value);
            rv = bos.toByteArray();
        } catch (Exception e) {
            log.error("serialize error %s", e);
        }
        return rv;
    }

    /**
     * 反序列化
     *
     * @param in 二进制数据
     * @return object 对象
     *  public static Object deserialize(byte[] in) {
     *         return deserialize(in,Object.class);
     *     }
     */


    /**
     * 反序列化
     *
     * @param in           二进制数据
     * @param <T>          t
     * @return t 对象
     */
    public static <T> T deserialize(byte[] in) {
        Object rv = null;
        try(
            ByteArrayInputStream bis = new ByteArrayInputStream(in);
            ObjectInputStream is = new ObjectInputStream(bis);
         ) {
            rv = is.readObject();
        } catch (Exception e) {
            log.error("serialize error %s", e);
        }
        return (T) rv;
    }

    //===========================序列与反序列 end===========

    //========================ID生成方法 start===========

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     *
     * @return string uuid
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 设置系统最大编号，同一时间最大编码，预设十万可足够，一少钟生成不了这么多单号随机数
     * 这里全部整体系统都用一个计算器，如果要分开表进行计算，再重新定义多个变量即可
     */
    private static final int ORDER_MAX_VALUE = 100000;

    /**
     * 读写锁操作
     */
    private static final ReentrantReadWriteLock OrderBuilderLock = new ReentrantReadWriteLock();

    /**
     * 单号计数器
     */
    private static int orderNumber;

    /**
     * 内部保证生成的字符串随机字字符串为“100000”六位的长度
     *
     * @param prefixCode 字符串
     * @return string 随机字字符串
     */
  /*  public static String newOrderNoByCode(String prefixCode) {
        OrderBuilderLock.readLock().lock();
        try {
            if (orderNumber >= ORDER_MAX_VALUE) {
                orderNumber = 0;
            }
            orderNumber += 1;

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String codeKey = formatter.format(new Date());
            return String.format("%s%s%d", prefixCode, codeKey, ORDER_MAX_VALUE + orderNumber);
        } finally {
            OrderBuilderLock.readLock().unlock();
        }
    }*/

  
   /**
    * 雪花算法
    * @author gq
    * @date 2022/7/27 19:37
   */
    public static String newOrderNoByCode(String prefixCode) {
        return  SnowFlake.nextId() +"";
    }

    /**
     * 通过Redis 生成唯一key 值
     *
     * @param prefixCode 字符串前缀，最多18 位，超部分将被截断。
     * @param redisKey   Redis key
     * @return
     */
    public static String makeUniqueId(String prefixCode, String redisKey) {
        if (prefixCode == null) {
            prefixCode = "";
        }
        if (StringUtils.isBlank(redisKey)) {
            redisKey = RedisEnum.RedisKeyEnum.SYSTEM_DEFAULT_ID_INCR_KEY.getCode();
        }
        if (prefixCode.length() > 18) {
            prefixCode = prefixCode.substring(0, 18);
        }
        Long incrOrderId = RedisUtil.incrBy(redisKey, 1);
        if (incrOrderId == 1) {
            // 每100天重置一次，避免达到上限
            RedisUtil.expire(redisKey, 100 * 24 * 60 * 60);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = sdf.format(new Date());
        return String.format("%s%s%04d", prefixCode, format, incrOrderId % 10000);
    }

    /**
     * 内部保证生成的字符串随机字字符串为“100000”六位的长度
     *
     * @return long long
     */
    public static long newOrderNo() {
        OrderBuilderLock.readLock().lock();
        try {
            if (orderNumber >= ORDER_MAX_VALUE) {
                orderNumber = 0;
            }
            orderNumber += 1;

            return System.currentTimeMillis() + orderNumber;
        } finally {
            OrderBuilderLock.readLock().unlock();
        }
    }

    /**
     * 获取6-10 的随机位数数字
     *
     * @param length 想要生成的长度
     * @return result 6-10的随机位数数字
     */
    public static String getRandom620(Integer length) {
        StringBuilder result = new StringBuilder();
        int n = 20;
        if (null != length && length > 0) {
            n = length;
        }
        int randInt = 0;
        for (int i = 0; i < n; i++) {
            randInt = rand.nextInt(10);
            result.append(randInt);
        }
        return result.toString();
    }

    //========================ID生成方法 end===========

//========================两个实体间的转换 start===========

    /**
     * {@code Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean}
     *
     * @param map map
     * @param obj obj
     */
    public static Object transMap2Bean(Map<String, Object> map, Object obj) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }
        } catch (Exception e) {
            log.error("transMap2Bean Error ", e);
        }
        return obj;
    }

    /**
     * {@code Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map}
     *
     * @param obj obj
     * @return map
     */
    public static Map<String, Object> transBean2Map(Object obj) {

        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>(16);
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!"class".equals(key)) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            log.error("transBean2Map Error ", e);
        }

        return map;

    }

    /**
     * SQL注入过滤
     *
     * @param str 待验证的字符串
     * @return 过滤字符串
     */
    public static String sqlInject(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        //去掉'|"|;|\字符
        str = StringUtils.replace(str, "'", "");
        str = StringUtils.replace(str, "\"", "");
        str = StringUtils.replace(str, ";", "");
        str = StringUtils.replace(str, "\\", "");

        //转换成小写
        str = str.toLowerCase();

        //非法字符
        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alert", "drop"};

        //判断是否包含非法字符
        for (String keyword : keywords) {
            if (str.contains(keyword)) {
                throw new IllegalArgumentException("包含非法字符");
            }
        }

        return str;
    }



    /**
     * 内部保证生成的字符串随机字字符串为“000”3位的长度
     *  private static final int NO_LENGTH = 3;
     * @param prefixCode 字符串
     * @return string 随机字字符串
     */
    public static String increment(String prefixCode) {
        OrderBuilderLock.readLock().lock();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
            String codeKey = formatter.format(new Date());
            Long increment = RedisUtil.incrBy(prefixCode + codeKey, 1);
            if (increment == null) {
                return null;
            }
            if (increment - 1 == 0) {
                RedisUtil.expire(prefixCode + codeKey, 86400);
            }
            String incrementStr = String.format("%03d", increment);
            return String.format("%s%s%s", prefixCode, codeKey, incrementStr);
        } finally {
            OrderBuilderLock.readLock().unlock();
        }
    }



    /**
     * 内部保证生成的字符串随机字字符串为“000”3位的长度 -- 封装方法
     *  private static final int NO_LENGTH_FOUR = 4;
     *
     * @param prefixCode 字符串
     * @return string 随机字字符串
     */
    public static String commonIncrement(String prefixCode, int num) {
        OrderBuilderLock.readLock().lock();
        try {
            Long increment = RedisUtil.incrBy(prefixCode, num);
            if (increment == null) {
                return null;
            }
            if (increment - 1 == 0) {
                RedisUtil.expire(prefixCode, 86400);
            }
            String incrementStr = String.format("%04d", increment);
            return String.format("%s%s", prefixCode, incrementStr);
        } finally {
            OrderBuilderLock.readLock().unlock();
        }
    }


    /**
     * 内部保证生成的字符串随机字字符串为“000”3位的长度
     *
     * @param prefixCode 字符串
     * @return string 随机字字符串
     */
    public static String incrementFour(String prefixCode) {
        return commonIncrement(prefixCode, 1);
    }

    /**
     * 内部保证生成的字符串随机字字符串为“000”3位的长度
     *
     * @param prefixCode 字符串
     * @return string 随机字字符串
     */
    public static String incrementCostCode(String prefixCode) {
        return commonIncrement(prefixCode, 17);
    }
}