package com.yl.server.serviceRegister;

import java.net.InetSocketAddress;

/**
 * @author yl
 * @date 2025-05-05 23:28
 */
public interface ServiceRegister {
    void register(Class<?> clazz, InetSocketAddress serviceAddress);
}
