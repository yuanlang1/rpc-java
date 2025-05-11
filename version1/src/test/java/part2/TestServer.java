package part2;

import part1.Common.service.Impl.UserServiceImpl;
import part1.Common.service.UserService;
import part1.Server.provider.ServiceProvider;
import part2.Server.server.NettyRPCRPCServer;

/**
 * @author yl
 * @date 2025-05-05 22:13
 */
public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);

        NettyRPCRPCServer rpcServer = new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
