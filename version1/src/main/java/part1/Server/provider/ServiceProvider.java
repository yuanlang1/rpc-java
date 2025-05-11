package part1.Server.provider;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author yl
 * @date 2025-05-04 17:17
 */
public class ServiceProvider {
    // 存放服务的实例
    private Map<String, Object> interfaceProvider;

    public ServiceProvider(){
        this.interfaceProvider = new HashMap<>();
    }

    // 本地注册服务
    public void provideServiceInterface(Object service){
        System.out.println("service = " + service);
        // 接口列表
        Class<?>[] interfaceName = service.getClass().getInterfaces();
        System.out.println("interfaceName = " + Arrays.toString(interfaceName));
        for (Class<?> clazz : interfaceName) {
            System.out.println("clazz = " + clazz);
            System.out.println("clazz.getName = " + clazz.getName());
            interfaceProvider.put(clazz.getName(), service);
        }
    }

    // 获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}
