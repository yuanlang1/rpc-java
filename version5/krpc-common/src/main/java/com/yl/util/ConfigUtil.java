package com.yl.util;

import cn.hutool.setting.dialect.Props;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.util.StrUtil;

/**
 * @author yl
 * @date 2025-05-07 22:17
 */
@Slf4j
public class ConfigUtil {
    // 加载配置文件 默认环境
    public static <T> T loadConfig(Class<T> targetClass, String prefix) {
        return loadConfig(targetClass, prefix, "");
    }

    // 加载配置文件 指定环境
    public static <T> T loadConfig(Class<T> targetClass, String prefix, String environment) {
        StringBuilder configFileNameBuilder = new StringBuilder("application");

        if (StrUtil.isNotBlank(environment)) {
            configFileNameBuilder.append("-").append(environment);
        }
        configFileNameBuilder.append(".properties");
        // 加载配置文件
        Props properties = new Props(configFileNameBuilder.toString());
        if (properties.isEmpty()) {
            log.warn("配置文件'{}'为空或加载失败", configFileNameBuilder.toString());
        } else {
            log.info("加载配置文件：{}", configFileNameBuilder.toString());
        }

        try {
            return properties.toBean(targetClass, prefix);
        } catch (Exception e) {
            log.error("配置转换失败，目标类：{}", targetClass.getName(), e);
            throw new RuntimeException("配置加载失败", e);
        }

    }
}
