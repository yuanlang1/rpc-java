package part3.Client.netty.nettyInitializer.Client.ServiceCenter;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author yl
 * @date 2025-05-05 23:02
 */
public class ZKServiceCenter implements ServiceCenter {
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRPC";

    public ZKServiceCenter() {
        // 指数时间重试
        ExponentialBackoffRetry policy = new ExponentialBackoffRetry(1000, 3); // 重试策略
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181") // 连接zookeeper地址
                .sessionTimeoutMs(40000)        // 会话超时时间
                .retryPolicy(policy)            // 重试策略
                .namespace(ROOT_PATH).build();  // 命名空间
        this.client.start();
        System.out.println("zookeeper 连接成功");
    }
    // 根据服务名返回地址
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName){
        try {
            List<String> stringList = client.getChildren().forPath("/" + serviceName);
            String s = stringList.get(0);
            System.out.println("serviceAddress = " + s);
            return parseAddress(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getServiceAddress(InetSocketAddress address){
        return address.getHostName() + ":" + address.getPort();
    }

    public InetSocketAddress parseAddress(String address){
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
