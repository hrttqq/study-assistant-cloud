package com.study.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.core.env.Environment;
import redis.clients.jedis.*;

import java.time.Duration;
import java.util.*;

/**
 * 使用第三方缓存服务器，处理二级缓存
 */
@Slf4j
public class RedisUtil {

    private RedisUtil() {
    }

    private static final String MSG_CN = "设置值失败：";
    private static final String JE_MSG_CN = "Jedis实例获取为空";
    private static final String GET_LEN_MSG_CN = "获取列表长度失败：";

    //#region 库号定义
    /**
     * Redis 数据库0号
     */
    private static final int DB0 = 0;
    /**
     * Redis 数据库1号
     */
    private static final int DB1 = 1;
    /**
     * Redis 数据库2号
     */
    private static final int DB2 = 2;
    /**
     * Redis 数据库3号
     */
    private static final int DB3 = 3;
    /**
     * Redis 数据库4号
     */
    private static final int DB4 = 4;
    /**
     * Redis 数据库5号
     */
    private static final int DB5 = 5;
    /**
     * Redis 数据库6号
     */
    private static final int DB6 = 6;
    /**
     * Redis 数据库7号
     */
    private static final int DB7 = 7;
    /**
     * Redis 数据库8号
     */
    private static final int DB8 = 8;
    /**
     * Redis 数据库9号
     */
    private static final int DB9 = 9;
    /**
     * Redis 数据库10号
     */
    private static final int DB10 = 10;
    /**
     * Redis 数据库11号
     */
    private static final int DB11 = 11;
    /**
     * Redis 数据库12号
     */
    private static final int DB12 = 12;
    /**
     * Redis 数据库13号
     */
    private static final int DB13 = 13;
    /**
     * Redis 数据库14号
     */
    private static final int DB14 = 14;
    /**
     * Redis 数据库15号
     */
    private static final int DB15 = 15;

    public static int getDB0() {
        return DB0;
    }

    public static int getDB1() {
        return DB1;
    }

    public static int getDB2() {
        return DB2;
    }

    public static int getDB3() {
        return DB3;
    }

    public static int getDB4() {
        return DB4;
    }

    public static int getDB5() {
        return DB5;
    }

    public static int getDB6() {
        return DB6;
    }

    public static int getDB7() {
        return DB7;
    }

    public static int getDB8() {
        return DB8;
    }

    public static int getDB9() {
        return DB9;
    }

    public static int getDB10() {
        return DB10;
    }

    public static int getDB11() {
        return DB11;
    }

    public static int getDB12() {
        return DB12;
    }

    public static int getDB13() {
        return DB13;
    }

    public static int getDB14() {
        return DB14;
    }

    public static int getDB15() {
        return DB15;
    }

    //#endregion

    //#region 变量定义
    /**
     * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
     */
    private static final int MAX_WAIT = 15 * 1000;
    /**
     * 超时时间
     */
    private static final int TIMEOUT = 10 * 1000;

    private static final String REDIS_HOST = "redis.host";
    private static final String REDIS_PASS = "redis.password";
    private static final String REDIS_MAX = "redis.maxIdle";

    /**
     * redis连接池定义
     */
    private static JedisPool jedisPool = null;

    private static JedisCluster jedisCluster = null;

    private static JedisSentinelPool jdisSentinelPool = null;

    private static int currentMode = 0;

    //#endregion

    /**
     * Jedis实例获取返回码
     *
     * @author jqlin
     */
    public static class JedisStatus {
        private JedisStatus() {
        }

        /**
         * Jedis实例获取失败
         */
        public static final long FAIL_LONG = -5L;
        /**
         * Jedis实例获取失败
         */
        public static final String FAIL_STRING = "-5";
    }

    public static void initialPool() {
        Environment props = SpringContextUtil.getApplicationContext().getEnvironment();
        //Redis服务器IP
        String host = props.getProperty(REDIS_HOST);
        boolean clusterRedisIs = Boolean.parseBoolean(props.getProperty("redis.cluster"));

        //配置为有中逗号的为集群，不然就是单机。
        if (host != null && host.split(",").length > 1) {
            if (clusterRedisIs) {
                log.info("执行1------------------------");
                currentMode = 2;
                initJedisClusterPool();
            } else {
                log.info("执行2------------------------");
                initJedisSentinelPool();
                currentMode = 1;
            }
        } else {
            log.info("执行3------------------------");
            initSinglePool();
            currentMode = 0;
        }

    }

    private static void initSinglePool() {
        Environment prop = SpringContextUtil.getApplicationContext().getEnvironment();
        //Redis服务器IP
        String host = prop.getProperty(REDIS_HOST);
        //Redis的端口号
        int port = NumberUtils.toInt(prop.getProperty("redis.port"), 6379);
        //访问密码
        String auth = prop.getProperty(REDIS_PASS);

        try {
            JedisPoolConfig config = new JedisPoolConfig();
            //最大连接数，如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            config.setMaxTotal(NumberUtils.toInt(prop.getProperty("redis.maxTotal"), 100));
            //最大空闲数，控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
            config.setMaxIdle(NumberUtils.toInt(prop.getProperty(REDIS_MAX), 30));
            //最小空闲数
            config.setMinIdle(NumberUtils.toInt(prop.getProperty("redis.minIdle"), 10));
            //是否在从池中取出连接前进行检验，如果检验失败，则从池中去除连接并尝试取出另一个
            config.setTestOnBorrow(true);
            //在return给pool时，是否提前进行validate操作
            config.setTestOnReturn(false);
            //在空闲时检查有效性，默认false
            config.setTestWhileIdle(true);
            /** 采用默认的
            //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；
            //这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
           // config.setMinEvictableIdleDuration(Duration.ofMillis(30000));
            //表示idle object evitor两次扫描之间要sleep的毫秒数
           // config.setTimeBetweenEvictionRuns(Duration.ofMillis(60000));
            //表示idle object evitor每次扫描的最多的对象数
           // config.setNumTestsPerEvictionRun(1000);
             **/
            //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWait(Duration.ofMillis(MAX_WAIT));

            if (StringUtils.isNotBlank(auth)) {
                jedisPool = new JedisPool(config, host, port, TIMEOUT, auth);
            } else {
                jedisPool = new JedisPool(config, host, port, TIMEOUT);
            }
        } catch (Exception e) {
            if (jedisPool != null) {
                jedisPool.close();
            }
            log.error("初始化Redis连接池失败", e);
        }
    }

    private static void initJedisClusterPool() {
        try {
            Environment props = SpringContextUtil.getApplicationContext().getEnvironment();
            String hostAndPorts = props.getProperty(REDIS_HOST);

            log.info("初始化redis集群,集群地址:" + hostAndPorts);

            if (jedisCluster == null) {
                //连接超时
                int connectTimeOut = 10000;
                //读写超时
                int soTimeOut = 5000;
                //重试次数
                int maxAttemts = 10;


                GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
                //访问密码
                String auth = props.getProperty(REDIS_PASS);
                //连接池最大连接数
                poolConfig.setMaxTotal(NumberUtils.toInt(props.getProperty("redis.maxActive"), 100));
                //连接池中最大空闲的连接数
                poolConfig.setMaxIdle(NumberUtils.toInt(props.getProperty(REDIS_MAX), 30));
                //当连接池用尽后，调用者的最大等待时间(单位为毫秒)，默认值为-1 表示永不超时，一直等待，一般不建议使用默认值。
                //向连接池借用连接时是否做连接有效性检测(ping),无效连接会被移除，每次借出多执行一次ping命令，默认false。
                poolConfig.setTestOnBorrow(false);
                //向连接池归还连接时是否做连接有效性检测(ping),无效连接会被移除，每次借出多执行一次ping命令，默认false。
                poolConfig.setTestOnReturn(true);
                //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
                setBlockWhenExhausted(poolConfig);

                Set<HostAndPort> jedisClusterNode = new HashSet<>();
                assert hostAndPorts != null;
                String[] hosts = hostAndPorts.split(",");

                for (String hostport : hosts) {
                    String[] ipport = hostport.split(":");

                    String ip = ipport[0];
                    int port = Integer.parseInt(ipport[1]);
                    jedisClusterNode.add(new HostAndPort(ip, port));
                }
                if (jedisCluster == null) {
                    if (StringUtils.isNotBlank(auth)) {
                        jedisCluster = new JedisCluster(jedisClusterNode, connectTimeOut, soTimeOut, maxAttemts, auth, poolConfig);

                    } else {
                        jedisCluster = new JedisCluster(jedisClusterNode, connectTimeOut, soTimeOut, maxAttemts, poolConfig);

                    }
                }
                log.info("初始化redis集群成功!");
            }
        } catch (Exception e) {
            log.error("初始化Redis集群失败", e);
        }
    }

    /**
     * redis-ip=10.253.234.7:26379,10.253.234.47:26379,10.253.234.48:26379
     * 主从三哨兵模式时配置是23679，而不是6379端口.
     */
    private static void initJedisSentinelPool() {
        try {
            Environment props = SpringContextUtil.getApplicationContext().getEnvironment();
            String sentinelHostAndPorts = props.getProperty(REDIS_HOST);
            String masterName = props.getProperty("redis.sentinel.monitor");
            if (StringUtils.isBlank(masterName)) {
                masterName = "mymaster";
            }
            log.info("初始化redis主从，地址:" + sentinelHostAndPorts);

            if (jdisSentinelPool == null || jdisSentinelPool.isClosed()) {
                //连接超时
                int connectTimeOut = 10000;
                //读写超时
                int soTimeOut = 5000;

                JedisPoolConfig poolConfig = new JedisPoolConfig();
                //访问密码
                String auth = props.getProperty(REDIS_PASS);
                //连接池最大连接数
                poolConfig.setMaxTotal(NumberUtils.toInt(props.getProperty("redis.maxActive"), 100));
                //连接池中最大空闲的连接数
                poolConfig.setMaxIdle(NumberUtils.toInt(props.getProperty(REDIS_MAX), 30));
                //默认
                poolConfig.setTestOnBorrow(false);
                poolConfig.setTestOnReturn(true);
                setBlockWhenExhausted(poolConfig);
                assert sentinelHostAndPorts != null;
                String[] hosts = sentinelHostAndPorts.split(",");
                Set<String> sentinelsList = new HashSet<>(Arrays.asList(hosts));
                if (jdisSentinelPool == null || jdisSentinelPool.isClosed()) {
                    jdisSentinelPool = new JedisSentinelPool(masterName, sentinelsList, poolConfig, connectTimeOut, soTimeOut, auth, Protocol.DEFAULT_DATABASE);
                    // jdisSentinelPool = new JedisSentinelPool(masterName, sentinelsList,auth);
                }

                log.info("初始化redis主从成功!");
            }
        } catch (Exception e) {
            log.error("初始化redis主从失败", e);
        }
    }

    private static void setBlockWhenExhausted(GenericObjectPoolConfig<?> poolConfig) {
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
        poolConfig.setJmxEnabled(true);
        poolConfig.setJmxNamePrefix("pool");
        poolConfig.setLifo(true);
        poolConfig.setMaxWait(Duration.ofMillis(-1));
        poolConfig.setMinEvictableIdleDuration(Duration.ofMillis(1800000));
        poolConfig.setMinIdle(10);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setSoftMinEvictableIdleDuration(Duration.ofMillis(1800000));
        poolConfig.setTestWhileIdle(false);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(-1));
    }


    //#region jedis 操作

    /**
     * 同步获取Jedis实例
     *
     * @return Jedis
     */
    public static Jedis getJedis() {
        Jedis jedis = null;
        try {
            switch (currentMode) {
                case 0:
                    if (jedisPool == null || jedisPool.isClosed()) {
                        initialPool();
                    } else {
                        jedis = jedisPool.getResource();
                    }
                    break;
                case 1:
                    if (jdisSentinelPool == null || jdisSentinelPool.isClosed()) {
                        log.info("主从getJedis()初始化缓存组");
                        initialPool();
                        log.info("主从getJedis()初始化缓存组");
                    } else {
                        jedis = jdisSentinelPool.getResource();
                        log.info("从主从中getJedis()获取到jedis" + jedis.info());
                    }
                    break;
                case 2:
                    if (jedisCluster == null) {
                        initialPool();
                    } else {
                        //随机
                        log.warn("Cluster mode does not support getting a raw Jedis resource. Use getJedisCluster() instead.");
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("同步获取Jedis实例失败" + e.getMessage(), e);
            returnBrokenResource(jedis);
        }

        return jedis;
    }

    @SuppressWarnings("deprecation")
    public static void returnResource(Jedis jedis) {
        try {
            if (jedis != null) {
                //为了不修改前面的代码，这里不每次都关闭资源，提升性能。
               jedis.close();
            }
        } catch (Exception e) {
            log.error("释放redis资源异常" + e.getMessage(), new Exception());
        }
    }

    @SuppressWarnings("deprecation")
    public static void returnBrokenResource(final Jedis jedis) {
        try {
            switch (currentMode) {
                case 0:
                    //if (jedis != null && jedisPool != null) {
                    if (jedisPool != null) {
                        jedisPool.close();
                    }
                    break;
                case 1:
                   // if (jedis != null && jdisSentinelPool != null) {
                    if (jdisSentinelPool != null) {
                        jdisSentinelPool.close();
                    }
                    break;
                case 2:
                    //if (jedis != null && jedisCluster != null) {
                    if (jedisCluster != null) {
                        jedisCluster.close();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("释放redis资源异常" + e.getMessage(), e);
        }
    }


    //#endregion

    //#region jedisCluster 操作


    public static JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public static JedisSentinelPool getJedisSentinelPool() {
        return jdisSentinelPool;
    }
    //#endregion

    /**
     * 设置值
     *
     * @param key   key
     * @param value 要设置的新值
     * @return {@code -5：Jedis实例获取失败<br/>OK：操作成功<br/>null：操作失败}
     * @author jqlin
     */
    public static String set(String key, String value) {
        String result = null;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return JedisStatus.FAIL_STRING;
        }
        try {
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error(MSG_CN + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 设置值
     *
     * @param key    key
     * @param value  要设置的新值
     * @param expire 过期时间，单位：秒
     * @return {@code  -5：Jedis实例获取失败<br/>OK：操作成功<br/>null：操作失败}
     * @author jqlin
     */
    public static String set(String key, String value, int expire) {
        String result = null;
        Jedis jedis = getJedis();
        if (jedis == null) {
            return JedisStatus.FAIL_STRING;
        }
        try {
            result = jedis.set(key, value);
            jedis.expire(key, expire);
        } catch (Exception e) {
            log.error(MSG_CN + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 获取值
     *
     * @param key key
     * @return 获取的value值
     * @author jqlin
     */
    public static String get(String key) {
        String result = null;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return JedisStatus.FAIL_STRING;
        }
        try {
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("获取值失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 设置key的过期时间
     *
     * @param key     key
     * @param seconds -5：Jedis实例获取失败，1：成功，0：失败
     * @return long
     * @author jqlin
     */
    public static long expire(String key, int seconds) {
        long result = 0;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return JedisStatus.FAIL_LONG;
        }
        try {
            result = jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error("设置{" + key + "}的过期时间失败", e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取key的剩余过期时间
     *
     * @param key key
     * @return 剩余过期时间（秒），-2：key不存在，-1：key存在但没有设置过期时间，-5：Jedis实例获取失败
     * @author jqlin
     */
    public static long ttl(String key) {
        long result = JedisStatus.FAIL_LONG;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.ttl(key);
        } catch (Exception e) {
            log.error("获取{" + key + "}的剩余过期时间失败", e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断key是否存在
     *
     * @param key key
     * @return boolean
     * @author jqlin
     */
    public static boolean exists(String key) {
        boolean result = false;

        Jedis jedis = getJedis();
        if (jedis == null) {
            log.warn(JE_MSG_CN);
            return false;
        }
        try {
            result = jedis.exists(key);
        } catch (Exception e) {
            log.error("判断{" + key + "}是否存在失败", e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除key
     *
     * @param keys 可变参数(jdk1.5新特性)
     * @return -5：Jedis实例获取失败，1：成功，0：失败
     * @author jqlin
     */
    public static long del(String... keys) {
        long result = JedisStatus.FAIL_LONG;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.del(keys);
        } catch (Exception e) {
            log.error("删除{" + keys + "}失败", e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * set if not exists，若key已存在，则setnx不做任何操作
     *
     * @param key   key
     * @param value key已存在，1：key赋值成功
     * @return long
     * @author jqlin
     */
    public static long setnx(String key, String value) {
        long result = JedisStatus.FAIL_LONG;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.setnx(key, value);
        } catch (Exception e) {
            log.error(MSG_CN + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * set if not exists，若key已存在，则setnx不做任何操作
     *
     * @param key    key
     * @param value  key已存在，1：key赋值成功
     * @param expire 过期时间，单位：秒
     * @return long
     * @author jqlin
     */
    public static long setnx(String key, String value, int expire) {
        long result = JedisStatus.FAIL_LONG;
        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.setnx(key, value);
            jedis.expire(key, expire);
        } catch (Exception e) {
            log.error(MSG_CN + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 设置过期时间戳
     *
     * @param key         key
     * @param value       值
     * @param msTimestamp
     * @return
     */
    public static boolean pDbExpireAt(String key, String value, long msTimestamp) {
        Jedis jedis = getJedis();
        if (jedis == null) {
            return false;
        }
        return setTimestamp(key, value, msTimestamp, jedis);
    }

    /**
     * 设置过期时间戳公共方法
     *
     * @param key
     * @param value
     * @param msTimestamp
     * @param jedis
     * @return boolean
     */
    private static boolean setTimestamp(String key, String value, long msTimestamp, Jedis jedis) {
        boolean flag = false;
        try {
            jedis.set(key, value);
            jedis.pexpireAt(key, msTimestamp);
            flag = true;
        } catch (Exception e) {
            log.error("设置过期时间戳失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return flag;
    }

    /**
     * 根据库号设置过期时间戳
     *
     * @param key         key
     * @param value       值
     * @param msTimestamp
     * @param dbIndex     数据库号
     * @return
     */
    public static boolean pDbExpireAtByDataBaseIndex(String key, String value, long msTimestamp, int dbIndex) {
        Jedis jedis = getJedis();
        if (jedis == null) {
            return false;
        }

        jedis.select(dbIndex);
        return setTimestamp(key, value, msTimestamp, jedis);
    }


    /**
     * 在列表key的头部插入元素
     *
     * @param key    key
     * @param values {@code  -5：Jedis实例获取失败，>0：返回操作成功的条数，0：失败}
     * @return long
     * @author jqlin
     */
    public static long lpush(String key, String... values) {
        long result = JedisStatus.FAIL_LONG;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.lpush(key, values);
        } catch (Exception e) {
            log.error("在列表key的头部插入元素失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 在列表key的尾部插入元素
     *
     * @param key    key
     * @param values {@code -5：Jedis实例获取失败，>0：返回操作成功的条数，0：失败}
     * @return long
     * @author jqlin
     */
    public static long rpush(String key, String... values) {
        long result = JedisStatus.FAIL_LONG;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.rpush(key, values);
        } catch (Exception e) {
            log.error("在列表key的尾部插入元素失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 返回存储在key列表的特定元素
     *
     * @param key   key
     * @param start 开始索引，索引从0开始，0表示第一个元素，1表示第二个元素
     * @param end   结束索引，-1表示最后一个元素，-2表示倒数第二个元素
     * @return redis client获取失败返回null
     * @author jqlin
     */
    public static List<String> lrange(String key, long start, long end) {
        List<String> result = null;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.lrange(key, start, end);
        } catch (Exception e) {
            log.error("查询列表元素失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 往redis里取list
     *
     * @param <T>     t
     * @param listKey list中的key
     * @param start   开始下标
     * @param end     结束下标
     * @return {@code List<T>} list数据
     */
    public static <T> List<T> getvbylist(String listKey, int start, int end) {
        Jedis jds = null;
        try {
            jds = jedisPool.getResource();
            jds.select(0);
            byte[] lkey = UtilityClass.serialize(listKey);
            List<T> list = new ArrayList<>();
            List<byte[]> xx = jds.lrange(lkey, start, end);
            for (byte[] bs : xx) {
                T t = UtilityClass.deserialize(bs);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            log.error("往redis里取list异常", e);
        } finally {
            returnResource(jds);
        }

        return new ArrayList<>();
    }

    /**
     * 获取列表长度
     *
     * @param key -5：Jedis实例获取失败
     * @return long 列表长度
     * @author jqlin
     */
    public static long llen(String key) {
        long result = 0;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.llen(key);
        } catch (Exception e) {
            log.error(GET_LEN_MSG_CN + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * {@code 移除等于value的元素
     * 当count>0时，从表头开始查找，移除count个；
     * 当count=0时，从表头开始查找，移除所有等于value的；
     * 当count<0时，从表尾开始查找，移除count个}
     *
     * @param key   key
     * @param count 数量
     * @param value 移除等于value的元素
     * @return -5:Jedis实例获取失败
     * @author jqlin
     */
    public static long lrem(String key, long count, String value) {
        long result = 0;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.lrem(key, count, value);
        } catch (Exception e) {
            log.error(GET_LEN_MSG_CN + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 对列表进行修剪
     *
     * @param key   key
     * @param start 开始下标
     * @param end   结束下标
     * @return -5：Jedis实例获取失败，OK：命令执行成功
     * @author jqlin
     */
    public static String ltrim(String key, long start, long end) {
        String result = "";

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.ltrim(key, start, end);
        } catch (Exception e) {
            log.error(GET_LEN_MSG_CN + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 设置对象
     *
     * @param <T> t
     * @param key key
     * @param obj 对象
     * @return 设置成功则返回对象 否则返回null
     * @author jqlin
     */
    public static <T> String setObject(String key, T obj) {
        String result = null;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            byte[] data = UtilityClass.serialize(obj);
            result = jedis.set(key.getBytes(), data);
        } catch (Exception e) {
            log.error("设置对象失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 获取对象
     *
     * @param <T> t
     * @param key key
     * @return t value对象
     * @author jqlin
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObject(String key) {
        T result = null;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            byte[] data = jedis.get(key.getBytes());
            if (data != null && data.length > 0) {
                result = (T) UtilityClass.deserialize(data);
            }
        } catch (Exception e) {
            log.error("获取对象失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 缓存Map赋值
     *
     * @param key   key
     * @param field 字段
     * @param value 字段值
     * @return -5：Jedis实例获取失败
     * @author jqlin
     */
    public static long hset(String key, String field, String value) {
        long result = 0L;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.hset(key, field, value);
        } catch (Exception e) {
            log.error("缓存Map赋值失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 获取缓存的Map值
     *
     * @param key   key
     * @param field map字段
     * @return 缓存的Map值
     */
    public static String hget(String key, String field) {
        String result = null;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.hget(key, field);
        } catch (Exception e) {
            log.error("获取缓存的Map值失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 获取map所有的字段和值
     *
     * @param key key
     * @return map所有的字段和值
     * @author jqlin
     */
    public static Map<String, String> hgetAll(String key) {
        Map<String, String> map = new HashMap<>(16);

        Jedis jedis = getJedis();
        if (jedis == null) {
            log.warn(JE_MSG_CN);
            return map;
        }
        try {
            map = jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("获取map所有的字段和值失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return map;
    }

    /**
     * 查看哈希表 key 中，指定的field字段是否存在。
     *
     * @param key   key
     * @param field 哈希表字段
     * @return boolean
     * @author jqlin
     */
    public static Boolean hexists(String key, String field) {
        boolean result = false;

        Jedis jedis = getJedis();
        if (jedis == null) {
            log.warn(JE_MSG_CN);
            return result;
        }
        try {
            result = jedis.hexists(key, field);
            return result;
        } catch (Exception e) {
            result = false;
            log.error("查看哈希表field字段是否存在失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
            return result;
        } finally {
            returnResource(jedis);
        }


    }

    /**
     * 自增
     *
     * @param key
     * @return
     */
    public static Long incr(String key) {
        long result = 0L;

        Jedis jedis = getJedis();
        if (jedis == null) {
            log.warn(JE_MSG_CN);
            return result;
        }
        try {
            return jedis.incr(key);
        } catch (Exception e) {
            log.error("查看哈希表field字段是否存在失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
            return null;
        } finally {
            returnResource(jedis);
        }


    }

    /**
     * 自增+步长
     *
     * @param key       自增key
     * @param increment 步长
     * @return 返回自增后的结果
     */
    public static Long incrBy(String key, long increment) {
        Jedis jedis = getJedis();
        if (jedis == null) {
            return null;
        }

        Long result = null;
        try {
            result = jedis.incrBy(key, increment);
        } catch (Exception e) {
            log.error("Redis自增失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 自增+步长+指定数据库
     *
     * @param key       自增key
     * @param increment 步长
     * @param index     指定数据库
     * @return 返回自增后的结果
     */
    public static Long incrFromDbBy(String key, int increment, int index) {
        Jedis jedis = getJedis();
        if (jedis == null) {
            return null;
        }

        jedis.select(index);
        Long result = null;
        try {
            result = jedis.incrBy(key, increment);
        } catch (Exception e) {
            log.error("Redis自增失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @param key key
     * @return 哈希表中的字段
     * @author jqlin
     */
    public static Set<String> hkeys(String key) {
        Set<String> set = new HashSet<>();
        Jedis jedis = getJedis();
        if (jedis == null) {
            log.warn(JE_MSG_CN);
            return set;
        }
        try {
            return jedis.hkeys(key);
        } catch (Exception e) {
            log.error("获取所有哈希表中的字段失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return new HashSet<>();
    }

    /**
     * 获取所有哈希表中的值
     *
     * @param key key
     * @return 哈希表中的值
     * @author jqlin
     */
    public static List<String> hvals(String key) {
        List<String> list = new ArrayList<>();

        Jedis jedis = getJedis();
        if (jedis == null) {
            log.warn(JE_MSG_CN);
            return list;
        }
        try {
            return jedis.hvals(key);
        } catch (Exception e) {
            log.error("获取所有哈希表中的值失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return list;
    }

    /**
     * 从哈希表 key 中删除指定的field
     *
     * @param key    key
     * @param fields 哈希表字段(可变参数 jdk1.5新特性)
     * @return long
     * @author jqlin
     */
    public static long hdel(String key, String... fields) {


        Jedis jedis = getJedis();
        if (jedis == null) {
            log.warn(JE_MSG_CN);
            return JedisStatus.FAIL_LONG;
        }
        try {
            return jedis.hdel(key, fields);
        } catch (Exception e) {
            log.error("map删除指定的field失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return 0;
    }

    public static Set<String> keys(String pattern) {
        Set<String> keyList = new HashSet<>();

        Jedis jedis = getJedis();
        if (jedis == null) {
            log.warn(JE_MSG_CN);
            return keyList;
        }
        try {
            keyList = jedis.keys(pattern);
        } catch (Exception e) {
            log.error("操作keys失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return keyList;
    }

    /**
     * 将数据设置到指定的库
     *
     * @param key   redis键
     * @param value redis值
     * @param index 数据库索引下标
     * @return java.lang.String
     * @author 赵万军
     * 创建时间 2019/12/12 18:03
     */
    public static String setIntoDb(String key, String value, int index) {
        String result = null;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return JedisStatus.FAIL_STRING;
        }
        jedis.select(index);
        try {
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error(MSG_CN + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 从指定的库中获取数据
     *
     * @param key   redis键
     * @param index 数据库索引下标
     * @return java.lang.String
     * @author 赵万军
     * 创建时间 2019/12/12 18:05
     */
    public static String getFromDb(String key, int index) {
        String result = null;

        Jedis jedis = getJedis();
        //先适应当前环境，此方法再一次初始化连接池再进行重新获取一次。
        if(jedis==null)
        {
            initialPool();
            jedis = getJedis();
        }
        if (jedis == null) {
            return JedisStatus.FAIL_STRING;
        }
        jedis.select(index);
        try {
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("获取值失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }

    /**
     * 从指定库中删除数据
     *
     * @param keys  redis键
     * @param index 数据库索引下标
     * @return long
     * @author 赵万军
     * 创建时间 2019/12/12 18:07
     */
    public static long delFromDb(String keys, int index) {
        long result = JedisStatus.FAIL_LONG;

        Jedis jedis = getJedis();
        if (jedis == null) {
            return result;
        }
        jedis.select(index);

        try {
            result = jedis.del(keys);
        } catch (Exception e) {
            log.error("删除{" + keys + "}失败：" + e.getMessage(), e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return result;
    }
}
