package com.yl.client.proxy;


import com.yl.client.serviceCenter.ServiceCenter;
import com.yl.client.serviceCenter.ZKServiceCenter;
import com.yl.client.circuitBreaker.CircuitBreaker;
import com.yl.client.circuitBreaker.CircuitBreakerProvider;
import com.yl.client.retry.guavaRetry;
import com.yl.client.rpcClient.Impl.NettyRpcClient;
import com.yl.client.rpcClient.RpcClient;
import com.yl.message.RpcRequest;
import com.yl.message.RpcResponse;
import com.yl.trace.interceptor.ClientTraceInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * @author yl
 * @date 2025-05-05 21:09
 */
@Slf4j
public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;
    private CircuitBreakerProvider circuitBreakerProvider;

    public ClientProxy() throws InterruptedException {
        serviceCenter = new ZKServiceCenter();
        circuitBreakerProvider = new CircuitBreakerProvider();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // trace记录
        ClientTraceInterceptor.beforeInvoke();
        // 构建RpcRequest
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();

        // 获取熔断器
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        // 判断熔断器是否通过
        if (!circuitBreaker.allowRequest()) {
            log.warn("熔断器开启，请求被拒绝：{}", request);
            return null;
        }
        String methodSignature = getMethodSignature(request.interfaceName, method);
        log.info("方法签名：{}", methodSignature);
        // 找到服务 缓存 或 zookeeper   其中包含负载均衡
        InetSocketAddress serviceAddress = serviceCenter.serviceDiscovery(request);
        log.info("serviceAddress：{}", serviceAddress.getHostName() + ":" + serviceAddress.getPort());
        rpcClient = new NettyRpcClient(serviceAddress);
        RpcResponse response;
        // 超时重传
        // 对白名单里面的服务进行重试
        if (serviceCenter.checkRetry(serviceAddress, methodSignature)) {
            try {
                log.info("尝试重试调用服务：{}", methodSignature);
                response = new guavaRetry().sendServiceWithRetry(request, rpcClient);
            } catch (Exception e) {
                log.error("重试调用失败：{}", methodSignature, e);
                circuitBreaker.recordFailure();
                throw e;
            }
        } else {
            response = rpcClient.sendRequest(request);
        }
        // 记录response状态，上报给熔断器
        if (response != null) {
            if (response.getCode() == 200) {
                circuitBreaker.recordSuccess();
            } else if (response.getCode() == 500) {
                circuitBreaker.recordFailure();
            }
            log.info("收到响应：{} 状态码：{}", request.getInterfaceName(), response.getCode());
        }
        // trace上报
        ClientTraceInterceptor.afterInvoke(method.getName());
        log.info("response：{}", response);
        return response != null ? response.getData() : null;
    }

    // 根据接口名字和方法获取方法签名
    private String getMethodSignature(String interfaceName, Method method) {
        StringBuilder builder = new StringBuilder();
        builder.append(interfaceName).append("#").append(method.getName()).append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            builder.append(parameterTypes[i].getName());
            if (i < parameterTypes.length - 1) {
                builder.append(",");
            } else {
                builder.append(")");
            }
        }
        return builder.toString();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }

    // 关闭创建的资源
    public void close(){
        rpcClient.close();
        serviceCenter.close();
    }
}
