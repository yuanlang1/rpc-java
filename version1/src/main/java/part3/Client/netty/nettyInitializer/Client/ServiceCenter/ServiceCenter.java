package part3.Client.netty.nettyInitializer.Client.ServiceCenter;

import java.net.InetSocketAddress;

/**
 * @author yl
 * @date 2025-05-05 23:01
 */
// 服务中心接口
public interface ServiceCenter {
    // 根据服务名返回地址
    InetSocketAddress serviceDiscovery(String serviceName);
}
