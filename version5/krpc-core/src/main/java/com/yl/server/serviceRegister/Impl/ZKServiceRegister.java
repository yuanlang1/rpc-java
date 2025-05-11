package com.yl.server.serviceRegister.Impl;

import com.yl.server.serviceRegister.ServiceRegister;
import com.yl.annotation.Retryable;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yl
 * @date 2025-05-05 23:29
 */
@Slf4j
public class ZKServiceRegister implements ServiceRegister {
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRPC";
    private static final String RETRY = "CanRetry";

    public ZKServiceRegister() {
        ExponentialBackoffRetry policy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH)
                .build();
        this.client.start();
        log.info("zookeeper 连接成功");
    }

    @Override
    public void register(Class<?> clazz, InetSocketAddress serviceAddress) {
        String serviceName = clazz.getName();
        try {
            // serviceName创建为永久节点 下线时不删服务名 删除地址
            if (client.checkExists().forPath("/" + serviceName) == null) {
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
                log.info("服务节点 {} 创建成功", "/" + serviceName);
            }
            // 路径地址
            String path = "/" + serviceName + "/" + getServiceAddress(serviceAddress);
            log.info("serviceName：{} Address：{}", serviceName, serviceAddress.getHostName() + ":" + serviceAddress.getPort());
            log.info("path：{}", path);
            if (client.checkExists().forPath(path) == null) {
                // 临时节点 服务器下线就删除
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                log.info("服务地址 {} 注册成功", path);
            } else {
                log.info("服务地址 {} 已经存在，跳过注册", path);
            }

            // 注册白名单 根据方法上的注解判断
            List<String> retryableMethods = getRetryableMethod(clazz);
            log.info("可重试的方法：{}", retryableMethods);
            CuratorFramework rootClient = client.usingNamespace(RETRY);
            for (String retryableMethod : retryableMethods) {
                rootClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                        .forPath("/" + getServiceAddress(serviceAddress) + "/" + retryableMethod);
            }
        } catch (Exception e) {
            log.error("服务注册失败，服务名：{}，错误信息：{}", serviceName, e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "zookeeper";
    }


    public String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }

    private InetSocketAddress parseAddress(String address) {
        String[] strings = address.split(":");
        return new InetSocketAddress(strings[0], Integer.parseInt(strings[1]));
    }

    // 判断一个方法是否添加了Retryable注解
    private List<String> getRetryableMethod(Class<?> clazz) {
        List<String> retryableMethods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Retryable.class)) {
                String methodSignature = getMethodSignature(clazz, method);
                retryableMethods.add(methodSignature);
            }
        }
        return retryableMethods;
    }

    private String getMethodSignature(Class<?> clazz, Method method) {
        StringBuilder builder = new StringBuilder();
        builder.append(clazz.getName()).append("#").append(method.getName()).append("(");
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
}
