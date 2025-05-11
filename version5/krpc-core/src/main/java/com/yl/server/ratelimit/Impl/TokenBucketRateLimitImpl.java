package com.yl.server.ratelimit.Impl;

import com.yl.server.ratelimit.RateLimit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yl
 * @date 2025-05-07 16:20
 */
@Slf4j
public class TokenBucketRateLimitImpl implements RateLimit {
    // 令牌生成速率 ms
    private final int rate;
    // 桶容量
    private final int capacity;
    // 当前桶容量
    private volatile int curCapacity;
    // 上次请求的时间戳
    private volatile long lastTimestamp;

    public TokenBucketRateLimitImpl(int rate, int capacity) {
        this.rate = rate;
        this.capacity = capacity;    // 桶最大容量
        this.curCapacity = capacity;     // 当前桶容量为最大容量
        this.lastTimestamp = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean getToken() {
        // 异步情况下
        synchronized (this) {
            if (curCapacity > 0) {
                curCapacity--;
                return true;
            }

            long current = System.currentTimeMillis();
            // 生成新的令牌 间隔时间 >= RATE
            if (current - lastTimestamp >= rate) {
                int generateTokens = (int) ((current - lastTimestamp) / rate);
                // 间隔时间可以生成多少个令牌
                if (generateTokens > 1) {
                    // 两倍以上
                    curCapacity = Math.min(capacity, curCapacity + generateTokens - 1);
                }
                // 更新时间戳
                lastTimestamp = current;
                return true;
            }
            return false;
        }
    }
}
