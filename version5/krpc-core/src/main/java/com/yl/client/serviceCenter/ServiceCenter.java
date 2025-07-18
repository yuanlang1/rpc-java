package com.yl.client.serviceCenter;

import com.yl.message.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author yl
 * @date 2025-05-05 23:01
 */
// 服务中心接口
public interface ServiceCenter {
    // 根据服务名返回地址
    InetSocketAddress serviceDiscovery(RpcRequest request);

    // 判断是否可以重试
    boolean checkRetry(InetSocketAddress serviceAddress, String methodSignature);

    // 关闭客户端
    void close();
}
