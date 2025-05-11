package part1.Server.serviceRegister.Impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import part1.Server.serviceRegister.ServiceRegister;

import java.net.InetSocketAddress;

/**
 * @author yl
 * @date 2025-05-05 23:29
 */
public class ZKServiceRegister implements ServiceRegister {
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRPC";
    private static final String RETRY = "CanRetry";

    public ZKServiceRegister() {
        ExponentialBackoffRetry policy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper 连接成功");
    }

    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress, boolean canRetry) {
        try {
            // serviceName创建为永久节点 下线时不删服务名 删除地址
            if (client.checkExists().forPath("/" + serviceName) == null) {
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
            }
            // 路径地址
            String path = "/" + serviceName + "/" + getServiceAddress(serviceAddress);
            // 临时节点 服务器下线就删除
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            // 可以重试，添加到重试中
            if (canRetry) {
                path = "/" + RETRY + "/" + serviceName;
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            }
        } catch (Exception e) {
            System.out.println("此服务已存在");
        }
    }

    public String getServiceAddress(InetSocketAddress serviceAddress) {
        return serviceAddress.getHostName() + ":" + serviceAddress.getPort();
    }

    private InetSocketAddress parseAddress(String addess){
        String[] strings = addess.split(":");
        return new InetSocketAddress(strings[0], Integer.parseInt(strings[1]));
    }

}
