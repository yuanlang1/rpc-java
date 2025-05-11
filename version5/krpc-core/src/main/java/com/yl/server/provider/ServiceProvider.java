package com.yl.server.provider;

import com.yl.server.serviceRegister.Impl.ZKServiceRegister;
import com.yl.server.serviceRegister.ServiceRegister;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yl
 * @date 2025-05-04 17:17
 */
@Slf4j
public class ServiceProvider {
    // 存放服务的实例
    private Map<String, Object> interfaceProvider;

    private String host;
    private int port;
    private ServiceRegister serviceRegister;
    // 限流器
    private RateLimitProvider rateLimitProvider;

    public ServiceProvider(String host, int port) {
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister = new ZKServiceRegister();
        this.rateLimitProvider = new RateLimitProvider();
    }

    // 本地注册服务
    public void provideServiceInterface(Object service) {
        String serviceName = service.getClass().getName();
        log.info("serviceName：{}", serviceName);
        // 接口列表
        Class<?>[] interfaceName = service.getClass().getInterfaces();
        log.info("interfaceNames：{}", (Object) interfaceName);
        for (Class<?> clazz : interfaceName) {
            // 本机映射表
            interfaceProvider.put(clazz.getName(), service);
            // 在注册中心注册
            log.info("register：{}", host + ":" + port);
            log.info("Clazz：{}", clazz);
            serviceRegister.register(clazz, new InetSocketAddress(host, port));
        }
    }

    // 获取服务实例
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }

    public RateLimitProvider getRateLimitProvider() {
        return rateLimitProvider;
    }
}
