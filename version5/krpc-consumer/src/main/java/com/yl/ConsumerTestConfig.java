package com.yl;

import com.yl.config.KRpcConfig;
import com.yl.util.ConfigUtil;

/**
 * @author yl
 * @date 2025-05-07 22:35
 */
public class ConsumerTestConfig {
    public static void main(String[] args) {
        KRpcConfig rpc = ConfigUtil.loadConfig(KRpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
