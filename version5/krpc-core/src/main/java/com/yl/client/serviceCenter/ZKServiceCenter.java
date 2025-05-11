package com.yl.client.serviceCenter;

import com.yl.client.serviceCenter.ZKWatcher.watchZK;
import com.yl.client.serviceCenter.balance.Impl.ConsistencyHashBalance;
import com.yl.client.serviceCenter.balance.LoadBalance;
import com.yl.client.cache.serviceCache;
import com.yl.message.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author yl
 * @date 2025-05-05 23:02
 */
@Slf4j
public class ZKServiceCenter implements ServiceCenter {
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRPC";
    private static final String RETRY = "CanRetry";
    private serviceCache cache;

    private final LoadBalance loadBalance = new ConsistencyHashBalance();

    private Set<String> retryServiceCache = new CopyOnWriteArraySet<>();

    public ZKServiceCenter() throws InterruptedException {
        // 指数时间重试
        ExponentialBackoffRetry policy = new ExponentialBackoffRetry(1000, 3); // 重试策略
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181") // 连接zookeeper地址
                .sessionTimeoutMs(40000)        // 会话超时时间
                .retryPolicy(policy)            // 重试策略
                .namespace(ROOT_PATH).build();  // 命名空间
        this.client.start();
        log.info("zookeeper 连接成功");
        // 初始化缓存
        cache = new serviceCache();
        // 加入zookeeper事件监听器
        watchZK watcher = new watchZK(client, cache);
        // 监听启动
        watcher.watchToUpdate(ROOT_PATH);
    }

    // 根据服务名返回地址
    @Override
    public InetSocketAddress serviceDiscovery(RpcRequest request) {
        String serviceName = request.getInterfaceName();
        try {
            // 先从缓存中找
            log.info("serviceName：{}", "/" + serviceName);
            List<String> addressList = cache.getServiceFromCache(serviceName);
            if (addressList == null || addressList.isEmpty()) {
                // 找不到 zookeeper中找
                log.info("serviceName：{}", "/" + serviceName);
                addressList = client.getChildren().forPath("/" + serviceName);
                List<String> cachedAddress = cache.getServiceFromCache(serviceName);
                if (cachedAddress == null || cachedAddress.isEmpty()) {
                    for (String address : addressList) {
                        cache.addServiceToCache(serviceName, address);
                    }
                }
            }
            if (addressList.isEmpty()) {
                log.warn("未找到服务：{}", serviceName);
                return null;
            }
            // String s = serviceList.get(0);
            // 负载均衡
            log.info("addressList：{}", addressList);
            String address = loadBalance.balance(addressList);
            log.info("serviceAddress：{}", address);
            return parseAddress(address);
        } catch (Exception e) {
            log.error("服务发现失败，服务名：{}", serviceName, e);
        }
        return null;
    }

    // 白名单检查
    @Override
    public boolean checkRetry(InetSocketAddress serviceAddress, String methodeSignature) {
        if (retryServiceCache.isEmpty()) {
            try {
                CuratorFramework rootClient = client.usingNamespace(RETRY);
                List<String> retryableMethods = rootClient.getChildren().forPath("/" + getServiceAddress(serviceAddress));
                retryServiceCache.addAll(retryableMethods);
            } catch (Exception e) {
                log.error("检测重试失败，方法签名：{}", methodeSignature, e);
            }
        }
        return retryServiceCache.contains(methodeSignature);
    }

    @Override
    public void close() {
        client.close();
    }

    public String getServiceAddress(InetSocketAddress address) {
        return address.getHostName() + ":" + address.getPort();
    }

    public InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
