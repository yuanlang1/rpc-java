package part1.Server.provider;

import part1.Server.ratelimit.Impl.TokenBucketRateLimitImpl;
import part1.Server.ratelimit.RateLimit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yl
 * @date 2025-05-07 16:31
 */
public class RateLimitProvider {
    // 接口名称与速率限制器实例映射关系
    private Map<String, RateLimit> rateLimitMap = new HashMap<>();

    public RateLimit getRateLimit(String interfaceName) {
        // 不存在，创建一个新的
        if (!rateLimitMap.containsKey(interfaceName)) {
            RateLimit rateLimit = new TokenBucketRateLimitImpl(100, 10);
            rateLimitMap.put(interfaceName, rateLimit);
            return rateLimit;
        }
        return rateLimitMap.get(interfaceName);
    }


}
