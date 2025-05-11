package com.yl.server.provider;


import com.yl.server.ratelimit.Impl.TokenBucketRateLimitImpl;
import com.yl.server.ratelimit.RateLimit;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yl
 * @date 2025-05-07 16:31
 */
@Slf4j
public class RateLimitProvider {
    // 接口名称与速率限制器实例映射关系
    private Map<String, RateLimit> rateLimitMap = new ConcurrentHashMap<>();

    // 默认的限流桶容量和令牌生成速率
    private static final int DEFAULT_CAPACITY = 100;
    private static final int DEFAULT_RATE = 10;

    public RateLimit getRateLimit(String interfaceName) {
        return rateLimitMap.computeIfAbsent(interfaceName, key->{
            RateLimit rateLimit = new TokenBucketRateLimitImpl(DEFAULT_CAPACITY, DEFAULT_RATE);
            log.info("为接口 [{}] 创建了新的限流策略：{}", interfaceName, rateLimit);
            return rateLimit;
        });
    }


}
