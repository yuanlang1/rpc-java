package com.yl.client.serviceCenter.balance.Impl;

import com.yl.client.serviceCenter.balance.LoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class ConsistencyHashBalance implements LoadBalance {
    // 虚拟节点的个数
    private static final int VIRTUAL_NUM = 5;

    // 虚拟节点分配，key是hash值，value是虚拟节点服务器名称
    private SortedMap<Integer, String> shards = new TreeMap<Integer, String>();

    // 真实节点列表
    private final List<String> realNodes = new CopyOnWriteArrayList<>();

    //模拟初始服务器
    private String[] servers = null;

    private void init(List<String> serviceList) {
        for (String server : serviceList) {
            realNodes.add(server);
            log.info("真实节点[{}] 被添加", server);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = server + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                log.info("虚拟节点[{}] hash:{}，被添加", virtualNode, hash);
            }
        }
    }

    // 获取被分配的节点名
    public String getServer(String node, List<String> serviceList) {
        if (shards.isEmpty()) {
            init(serviceList);
        }
        int hash = getHash(node);
        Integer key = null;
        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()) {
            key = shards.firstKey();
        } else {
            key = subMap.firstKey();
        }
        String virtualNode = shards.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    // 添加节点
    public void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            log.info("真实节点 [{}] 被添加", node);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                log.info("虚拟节点 [{}}] hash:{}}，被添加", virtualNode, node);
            }
        }
    }


    // 删除节点
    public void delNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            log.info("真实节点 [{}}] 下线移除", node);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                log.info("虚拟节点[{}}] hash:{}}，被移除", virtualNode, node);
            }
        }
    }

    // 获取hash值
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    @Override
    public String balance(List<String> addressList) {
        if (addressList == null || addressList.isEmpty()) {
            throw new IllegalArgumentException("Address list cannot be null or empty");
        }
        String random = UUID.randomUUID().toString();
        return getServer(random, addressList);
    }

    @Override
    public String toString() {
        return "ConsistencyHash";
    }

    public SortedMap<Integer, String> getShards() {
        return shards;
    }

    public List<String> getRealNodes() {
        return realNodes;
    }

}