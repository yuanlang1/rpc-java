package part3.Server.serviceRegister;

import java.net.InetSocketAddress;

/**
 * @author yl
 * @date 2025-05-05 23:28
 */
public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress serviceAddress);
}
