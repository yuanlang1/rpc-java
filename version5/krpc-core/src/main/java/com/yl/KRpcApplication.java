package com.yl;

import com.yl.config.KRpcConfig;
import com.yl.config.RpcConstant;
import com.yl.util.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yl
 * @date 2025-05-07 22:27
 */
@Slf4j
public class KRpcApplication {
    private static volatile KRpcConfig rpcConfigInstance;

    public static void initialize(KRpcConfig customRpcConfig) {
        rpcConfigInstance = customRpcConfig;
        log.info("RPC 框架初始化，配置 = {}", customRpcConfig);
    }

    public static void initialize() {
        KRpcConfig customRpcConfig;

        try {
            customRpcConfig = ConfigUtil.loadConfig(KRpcConfig.class, RpcConstant.CONFIG_FILE_PREFIX);
            log.info("成功加载配置文件，配置文件 = {}", RpcConstant.CONFIG_FILE_PREFIX);
        } catch (Exception e) {
            // 加载失败 使用默认配置
            customRpcConfig = new KRpcConfig();
            log.warn("配置加载失败，使用默认配置");
        }
        initialize(customRpcConfig);
    }

    public static KRpcConfig getRpcConfig() {
        if (rpcConfigInstance == null) {
            synchronized ((KRpcApplication.class)){
                if (rpcConfigInstance == null) {
                    initialize(); // 确保在第一次调用时初始化
                }
            }
        }
        return rpcConfigInstance;
    }
}
