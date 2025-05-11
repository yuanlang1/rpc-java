package com.yl.client.retry;

import com.github.rholder.retry.*;
import com.yl.client.rpcClient.RpcClient;
import com.yl.message.RpcRequest;
import com.yl.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author yl
 * @date 2025-05-07 14:22
 */
@Slf4j
public class guavaRetry {
    // 发送Rpc请求
    private RpcClient rpcClient;

    public RpcResponse sendServiceWithRetry(RpcRequest request, RpcClient client) {
        this.rpcClient = client;
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                // 所有异常都重试
                .retryIfException()
                // 重试会在请求发送异常或返回代码为500
                .retryIfResult(response -> Objects.equals(response.getCode(), 500))
                // 等待策略，每次重试等待2s
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                // 重试停止策略，最多重试3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                // 重试监听器
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试第 {} 次", attempt.getAttemptNumber());
                    }
                })
                .build();
        try {
            // retryer.call 执行RPC请求，进行重试
            // Lambda表达式为可执行操作 Callable
            log.info("retryer.call()");
            log.info("request：{}", request);
            return retryer.call(() -> rpcClient.sendRequest(request));
        } catch (ExecutionException | RetryException e) {
            log.error("重试失败：请求 {} 执行时遇见异常", request.getMethodName(), e);
        }
        return RpcResponse.fail("重试失败，所有重试尝试已结束");
    }
}
