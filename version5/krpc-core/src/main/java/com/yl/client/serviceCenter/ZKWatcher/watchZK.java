package com.yl.client.serviceCenter.ZKWatcher;

import com.yl.client.cache.serviceCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

/**
 * @author yl
 * @date 2025-05-06 16:44
 */
@Slf4j
public class watchZK {
    // zk客户端
    private CuratorFramework client;
    // 服务缓存
    serviceCache cache;

    public watchZK(CuratorFramework client, serviceCache cache) {
        this.client = client;
        this.cache = cache;
    }

    // 监听当前节点和子节点的更新，创建，删除
    public void watchToUpdate(String path) {
        // 监听指定路径下的节点变化，并在节点变化后更新本地缓存
        // 监听节点的api
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            // type 为事件类型
            // 第二个参数为节点更新前状态，数据  第三个为更新后状态，数据
            // 创建节点时，第二个参数为null  删除节点时，第三个参数为null
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {
                switch (type.name()) {
                    // 节点创建
                    case "NODE_CREATED":
                        log.info("NODE_CREATED");
                        String[] pathList = pasrePath(childData1);
                        if (pathList.length <= 2) {
                            break;
                        } else {
                            String serviceName = pathList[1];
                            String address = pathList[2];
                            cache.addServiceToCache(serviceName, address);
                            log.info("节点创建：服务名称 {} 地址 {}", serviceName, address);
                        }
                        break;
                    // 节点更新
                    case "NODE_CHANGED":
                        log.info("NODE_CHANGED");
                        if (childData.getData() != null) {
                            log.debug("修改前的数据：{}", new String(childData.getData()));
                        } else {
                            log.debug("节点第一次赋值");
                        }
                        String[] oldPathList = pasrePath(childData);
                        String[] newPathList = pasrePath(childData1);
                        cache.replaceServiceAddress(oldPathList[1], oldPathList[2], newPathList[2]);
                        log.info("节点更新：服务名称 {} 地址从 {} 更新为 {}", oldPathList[1], oldPathList[2], newPathList[2]);
                        break;
                    //  节点删除
                    case "NODE_DELETED":
                        log.info("NODE_DELETED");
                        String[] pathList_d = pasrePath(childData);
                        if (pathList_d.length <= 2) {
                            break;
                        } else {
                            String serviceName = pathList_d[1];
                            String address = pathList_d[2];
                            // 删除缓存
                            cache.delete(serviceName, address);
                            log.info("节点删除：服务名称 {} 地址 {}", serviceName, address);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        // 开启监听
        curatorCache.start();
    }

    // 解析节点对应地址
    public String[] pasrePath(ChildData childData) {
        // 获取更新的节点路径
        String path = new String(childData.getData());
        log.info("节点路径：{}", path);
        return path.split("/");
    }
}
