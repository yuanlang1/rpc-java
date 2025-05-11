package com.yl.client.circuitBreaker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yl
 * @date 2025-05-07 16:55
 */
@Slf4j
public class CircuitBreaker {
    // 熔断器初始状态
    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    // 失败请求计数
    private AtomicInteger failureCount = new AtomicInteger(0);
    // 请求成功计数
    private AtomicInteger successCount = new AtomicInteger(0);
    // 请求总数
    private AtomicInteger requestCount = new AtomicInteger(0);
    // 失败阈值
    private final int failureThreshold;
    // 半开状态下的成功阈值
    private final double halfOpenSuccessRate;
    // 重置时间周期
    private final long resetTimePeriod;
    // 最后一次失败时间
    private long lastFailureTime = 0;


    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate, long resetTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.resetTimePeriod = resetTimePeriod;
    }

    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        // 根据熔断器状态判断是否允许请求
        switch (state) {
            case OPEN:
                // 打开状态 -> 半开状态
                if (currentTime - lastFailureTime > resetTimePeriod) {
                    state = CircuitBreakerState.HALF_OPEN;
                    resetCounts();  // 重置
                    log.info("熔断已解除，进入半开启状态，允许请求通过");
                    return true; // 允许请求
                }
                log.warn("熔断生效中，拒绝请求");
                return false;   // 继续熔断
            case HALF_OPEN:
                // 半开状态下记录请求
                requestCount.incrementAndGet();
                log.info("当前为半开启状态，计数请求");
                return true;
            case CLOSED:
            default:
                log.info("当前为正常状态，允许请求通过");
                return true; // 服务正常，拒绝请求
        }
    }

    // 记录一次成功的请求
    public synchronized void recordSuccess() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            successCount.incrementAndGet();
            // 大于阈值
            if (successCount.get() >= halfOpenSuccessRate * requestCount.get()) {
                // 恢复正常状态
                state = CircuitBreakerState.CLOSED;
                resetCounts();
                log.info("成功次数已达阈值，熔断器切换至关闭状态");
            }
        } else {
            // 非半开状态，重置
            resetCounts();
            log.info("熔断器处于关闭状态，重置计数器");
        }
    }

    // 记录一次失败的请求
    public synchronized void recordFailure() {
        failureCount.incrementAndGet();
        log.info("记录失败，当前失败次数：{}", failureCount);
        lastFailureTime = System.currentTimeMillis();
        if (state == CircuitBreakerState.HALF_OPEN) {
            // 半开失败，切换到打开状态
            state = CircuitBreakerState.OPEN;
            lastFailureTime = System.currentTimeMillis();
            log.warn("半开启状态下失效，熔断器切换至开启状态");
        } else if (failureCount.get() >= failureThreshold) {
            // 失败超过阈值
            state = CircuitBreakerState.OPEN;
            log.error("失败次数已超过阈值，熔断器切换至开启状态");
        }
    }

    public CircuitBreakerState getState() {
        return state;
    }

    public void resetCounts() {
        failureCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }
}

