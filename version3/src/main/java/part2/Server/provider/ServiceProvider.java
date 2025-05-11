package part2.Server.provider;

import part2.Server.serviceRegister.Impl.ZKServiceRegister;
import part2.Server.serviceRegister.ServiceRegister;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yl
 * @date 2025-05-04 17:17
 */
public class ServiceProvider {
    // 存放服务的实例
    private Map<String, Object> interfaceProvider;

    private String host;
    private int port;
    private ServiceRegister serviceRegister;

    public ServiceProvider(String host, int port) {
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister = new ZKServiceRegister();
    }

    // 本地注册服务
    public void provideServiceInterface(Object service, boolean canRetry) {
        System.out.println("service = " + service);
        // 接口列表
        Class<?>[] interfaceName = service.getClass().getInterfaces();
        System.out.println("interfaceName = " + Arrays.toString(interfaceName));
        for (Class<?> clazz : interfaceName) {
            System.out.println("clazz = " + clazz);
            System.out.println("clazz.getName = " + clazz.getName());
            // 本机映射表
            interfaceProvider.put(clazz.getName(), service);
            // 在注册中心注册
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port), canRetry);
        }
    }

    // 获取服务实例
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}
