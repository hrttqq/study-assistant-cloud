package com.study.user.api.utils;

import com.study.common.core.utils.RedisUtil;
import com.study.user.api.UserNoExhaustedException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;


@Component
@RequiredArgsConstructor
public class UserIdGenerator {

    /**
     * 2位固定前缀
     */
    private static final String PREFIX = "10";

    /**
     * 桶数量：0 ~ 9
     */
    private static final int BUCKET_COUNT = 10;

    /**
     * 每个桶容量：
     * 00000 ~ 99999
     *
     * 共100000个
     */
    private static final long BUCKET_CAPACITY = 100_000L;

    /**
     * 8位用户ID总容量：
     *
     * 10个桶 × 100000
     * = 100万个
     */
    private static final long TOTAL_CAPACITY =
            BUCKET_CAPACITY * BUCKET_COUNT;

    /**
     * Redis全局自增序列
     */
    private static final String REDIS_KEY
            = "user:id:global:sequence";

    /**
     * 置换算法乘数。
     *
     * 必须与100000互质。
     */
    private static final long A = 7919L;

    /**
     * 每个桶使用不同偏移量。
     */
    private static final long[] BUCKET_OFFSET = {
            52741L,
            18326L,
            76195L,
            40817L,
            93621L,
            27468L,
            81532L,
            64917L,
            35782L,
            70843L
    };


    /**
     * 启动时检查参数是否合法。
     */
    @PostConstruct
    public void validate() {

        if (BUCKET_OFFSET.length != BUCKET_COUNT) {
            throw new IllegalStateException(
                    "BUCKET_OFFSET数量必须等于BUCKET_COUNT"
            );
        }

        if (gcd(A, BUCKET_CAPACITY) != 1) {
            throw new IllegalStateException(
                    "置换参数A必须与BUCKET_CAPACITY互质"
            );
        }
        for (long offset : BUCKET_OFFSET) {
            if (offset < 0 || offset >= BUCKET_CAPACITY) {
                throw new IllegalStateException(
                        "bucket偏移量必须在0~99999之间"
                );
            }
        }
    }



    /**
     * 生成用户ID。
     *
     * 格式：
     *
     * 10 | 3 | 52741
     *
     * ↓
     *
     * "10352741"
     *
     * @return String类型用户ID
     */
    public static String generate() {

        Jedis jedis = RedisUtil.getJedis();

        if (jedis == null) {
            throw new IllegalStateException("获取Redis连接失败");
        }

        try {
            // Redis INCR，原子自增
            Long current = jedis.incr(REDIS_KEY);

            // 转成从0开始的序列
            long globalSequence = current - 1;

            // 判断8位用户ID是否已经全部使用
            if (globalSequence >= TOTAL_CAPACITY) {
                throw new UserNoExhaustedException(
                        "8位用户ID已全部使用完毕"
                );
            }

            // 计算桶：0 ~ 9
            int bucket =
                    (int) (globalSequence % BUCKET_COUNT);

            // 当前桶内部序列：0 ~ 99999
            long bucketSequence =
                    globalSequence / BUCKET_COUNT;

            // 打散
            long suffix =
                    shuffle(bucketSequence, bucket);

            // 拼接最终用户ID
            return PREFIX
                    + bucket
                    + String.format("%05d", suffix);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "生成用户ID失败",
                    e
            );
        } finally {
            RedisUtil.returnResource(jedis);
        }
    }



    /**
     * 对桶内序列进行置换打散。
     */
    private static long shuffle(
            long sequence,
            int bucket
    ) {

        long offset =
                BUCKET_OFFSET[bucket];

        return (
                A * sequence + offset
        ) % BUCKET_CAPACITY;
    }

    /**
     * 最大公约数。
     */
    private static long gcd(
            long a,
            long b
    ) {

        while (b != 0) {

            long temp = a % b;

            a = b;
            b = temp;
        }

        return a;
    }
}
