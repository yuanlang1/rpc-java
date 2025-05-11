package part2;

import part2.Common.service.Impl.UserServiceImpl;
import part2.Common.service.UserService;
import part2.Server.provider.ServiceProvider;
import part2.Server.server.NettyRPCRPCServer;
import part2.Server.server.RpcServer;

/**
 * @author yl
 * @date 2025-05-06 12:23
 */
public class TestServer {
    public static void main(String[] args) {
        UserService service = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9998);
        serviceProvider.provideServiceInterface(service);

        RpcServer rpcServer = new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9998);
    }
}
