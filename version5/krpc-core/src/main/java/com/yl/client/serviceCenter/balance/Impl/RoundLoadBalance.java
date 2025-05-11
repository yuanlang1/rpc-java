package com.yl.client.serviceCenter.balance.Impl;

import com.yl.client.serviceCenter.balance.LoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yl
 * @date 2025-05-07 10:44
 */
@Slf4j
public class RoundLoadBalance implements LoadBalance {
    private List<String> addressList = new CopyOnWriteArrayList<>();
    private AtomicInteger choose = new AtomicInteger(0);

    @Override
    public String balance(List<String> addressList) {
        if (addressList == null || addressList.isEmpty()) {
            throw new IllegalArgumentException("Address list cannot be null or empty");
        }
        int currentChoose = choose.getAndUpdate(i -> (i + 1) % addressList.size());
        String selectedServer = addressList.get(currentChoose);
        System.out.println("负载均衡选择了" + selectedServer + "服务器");
        return selectedServer;
    }

    @Override
    public void addNode(String node) {
        addressList.add(node);
        log.info("节点 {} 已加入负载均衡", node);
    }

    @Override
    public void delNode(String node) {
        addressList.remove(node);
        log.info("节点 {} 已从负载均衡中移除", node);
    }
}
