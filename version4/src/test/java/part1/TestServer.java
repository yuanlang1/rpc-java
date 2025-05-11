package part1;


import part1.Common.service.Impl.UserServiceImpl;
import part1.Common.service.UserService;
import part1.Server.provider.ServiceProvider;
import part1.Server.server.NettyRPCRPCServer;
import part1.Server.server.RpcServer;

/**
 * @author yl
 * @date 2025-05-06 12:23
 */
public class TestServer {
    public static void main(String[] args) {
        UserService service = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(service, true);

        RpcServer rpcServer = new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
