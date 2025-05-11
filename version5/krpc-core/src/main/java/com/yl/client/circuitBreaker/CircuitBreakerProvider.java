package com.yl.client.circuitBreaker;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yl
 * @date 2025-05-07 19:43
 */
@Slf4j
public class CircuitBreakerProvider {
    // 存储每个服务的熔断器实例，key为服务名 value为该服务的熔断器
    private Map<String, CircuitBreaker> circuitBreakerMap = new ConcurrentHashMap<>();

    // 根据服务名获取对应的熔断器
    public synchronized CircuitBreaker getCircuitBreaker(String serviceName) {
        return circuitBreakerMap.computeIfAbsent(serviceName, key ->{
            log.info("服务器[{}]不存在熔断器，创建新的熔断器实例", serviceName);
            // 创建并返回新熔断器
            return new CircuitBreaker(1, 0.5, 10000);
        });
    }

}
