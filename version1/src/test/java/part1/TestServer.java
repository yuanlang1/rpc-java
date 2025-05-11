package part1;

import org.junit.Test;
import part1.Common.service.Impl.UserServiceImpl;
import part1.Common.service.UserService;
import part1.Server.provider.ServiceProvider;
import part1.Server.server.Impl.SimpleRPCRPCServer;

/**
 * @author yl
 * @date 2025-05-05 14:51
 */
public class TestServer {
    public static void main(String[] args){
        // 创建服务实现类
        UserService userService = new UserServiceImpl();
        // 实例化服务注册中心
        ServiceProvider serviceProvider = new ServiceProvider();
        // 注册服务
        serviceProvider.provideServiceInterface(userService);
        // 实例化服务端
        SimpleRPCRPCServer rpcServer = new SimpleRPCRPCServer(serviceProvider);
        // 启动服务端
        rpcServer.start(9999);
    }
}
