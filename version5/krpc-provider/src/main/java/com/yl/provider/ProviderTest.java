package com.yl.provider;

import com.yl.KRpcApplication;
import com.yl.server.provider.ServiceProvider;
import com.yl.server.server.Impl.NettyRPCRPCServer;
import com.yl.provider.Impl.UserServiceImpl;
import com.yl.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yl
 * @date 2025-05-08 11:20
 */
@Slf4j
public class ProviderTest {
    public static void main(String[] args) {
        KRpcApplication.initialize();
        String host = KRpcApplication.getRpcConfig().getHost();
        int port = KRpcApplication.getRpcConfig().getPort();
        log.info("host：{} port：{}", host, port);
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider(host, port);

        serviceProvider.provideServiceInterface(userService);

        NettyRPCRPCServer server = new NettyRPCRPCServer(serviceProvider);
        server.start(port);
        log.info("RPC 服务端启动，监听接口 {}", port);
    }
}
