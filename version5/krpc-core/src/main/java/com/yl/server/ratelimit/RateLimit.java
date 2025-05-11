package com.yl.server.ratelimit;

/**
 * @author yl
 * @date 2025-05-07 16:19
 */
public interface RateLimit {
    // 获取访问许可
    boolean getToken();
}
