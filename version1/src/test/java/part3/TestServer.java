package part3;

import part1.Common.service.Impl.UserServiceImpl;
import part1.Common.service.UserService;
import part3.Server.provider.ServiceProvider;
import part3.Server.server.NettyRPCRPCServer;

/**
 * @author yl
 * @date 2025-05-05 22:13
 */
public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService);

        NettyRPCRPCServer rpcServer = new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
