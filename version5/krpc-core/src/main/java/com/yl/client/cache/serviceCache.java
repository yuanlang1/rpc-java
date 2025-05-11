package com.yl.client.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yl
 * @date 2025-05-06 16:07
 */
@Slf4j
public class serviceCache {
    private static Map<String, List<String>> cache = new ConcurrentHashMap<>();

    // 添加服务
    public void addServiceToCache(String serviceName, String address) {
        if (cache.containsKey(serviceName)) {
            List<String> addressList = cache.get(serviceName);
            addressList.add(address);
            log.info("有服务名情况，将name为{}和地址为{}服务添加到本地缓存中", serviceName, address);
        } else {
            List<String> addressList = new ArrayList<>();
            addressList.add(address);
            cache.put(serviceName, addressList);
            log.info("无服务名情况，将name为{}和地址为{}的服务添加到本地缓存中", serviceName, address);
        }
    }

    // 修改服务
    public void replaceServiceAddress(String serviceName, String oldAddress, String newAddress) {
        if (cache.containsKey(serviceName)) {
            List<String> addresList = cache.get(serviceName);
            addresList.remove(oldAddress);
            addresList.add(newAddress);
            log.info("将服务{}的地址{}替换为{}", serviceName, oldAddress, newAddress);
        } else {
            log.error("旧地址{}不在服务{}的地址列表中", oldAddress,  serviceName);
        }
    }

    // 从缓存中获取服务地址
    public List<String> getServiceFromCache(String serviceName) {
        if (!cache.containsKey(serviceName)) {
            log.warn("服务{}未找到", serviceName);
            return Collections.emptyList();
        } else {
            return cache.get(serviceName);
        }
    }

    // 从缓存中删除服务地址
    public void delete(String serviceName, String address) {
        List<String> addressList = cache.get(serviceName);
        if (addressList != null && addressList.contains(address)) {
            addressList.remove(address);
            log.info("将name为{}和地址为{}的服务从本地缓存中删除", serviceName, address);
            if (addressList.isEmpty()) {
                cache.remove(serviceName);
                log.info("服务{}的地址列表为空，已从缓存中清除", serviceName);
            } else {
                log.warn("删除失败，地址{}不在服务{}的地址列表中", address, serviceName);
            }
        }
    }
}
