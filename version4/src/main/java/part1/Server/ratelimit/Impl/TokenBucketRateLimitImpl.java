package part1.Server.ratelimit.Impl;

import part1.Server.ratelimit.RateLimit;

/**
 * @author yl
 * @date 2025-05-07 16:20
 */
public class TokenBucketRateLimitImpl implements RateLimit {
    // 令牌生成速率
    private static int RATE;
    // 桶容量
    private static int CAPACITY;
    // 当前桶容量
    private volatile int curCapacity;
    // 上次请求的时间戳
    private volatile long timeStamp = System.currentTimeMillis();

    public TokenBucketRateLimitImpl(int rate, int capacity) {
        RATE = rate;
        CAPACITY = capacity;    // 桶最大容量
        curCapacity = capacity;     // 当前桶容量为最大容量
    }

    @Override
    public synchronized boolean getToken() {
        // 当前有令牌，直接消费一个
        if (curCapacity > 0) {
            curCapacity--;
            return true;
        }
        long current = System.currentTimeMillis();

        // 生成新的令牌 间隔时间>=RATE
        if (current - timeStamp >= RATE) {
            // 间隔时间可以生成多少个令牌
            if ((current - timeStamp) / RATE >= 2) {
                // 两倍以上
                curCapacity += (int) ((current - timeStamp) / RATE) - 1;
            }
            // 保证小于最大值
            if (curCapacity > CAPACITY) {
                curCapacity = CAPACITY;
            }

            // 更新时间戳
            timeStamp = current;
            return true;
        }
        return  false;
    }
}
