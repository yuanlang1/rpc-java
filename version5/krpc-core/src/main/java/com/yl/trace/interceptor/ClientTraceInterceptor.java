package com.yl.trace.interceptor;

import com.yl.trace.TraceContext;
import com.yl.trace.TraceIdGenerator;
import com.yl.trace.ZipkinReporter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yl
 * @date 2025-05-10 12:23
 */
@Slf4j
public class ClientTraceInterceptor {
    // 上传之前 初始化和保存日志链路数据
    public static void beforeInvoke() {
        String traceId = TraceContext.getTraceId();
        // 生成TraceId
        if (traceId == null) {
            traceId = TraceIdGenerator.generateTraceId();
            TraceContext.setTraceId(traceId);
        }
        String spanId = TraceIdGenerator.generateSpanId();
        TraceContext.setSpanId(spanId);

        long startTimestamp = System.currentTimeMillis();
        TraceContext.setStartTimestamp(String.valueOf(startTimestamp));
    }

    // 上传日志链路数据
    public static void afterInvoke(String serviceName) {
        long endTimestamp = System.currentTimeMillis();
        long startTimestamp = Long.valueOf(TraceContext.getStartTimestamp());
        long duration = endTimestamp - startTimestamp;

        // 上报
        ZipkinReporter.reportSpan(
                TraceContext.getTraceId(),
                TraceContext.getSpanId(),
                TraceContext.getParentSpanId(),
                "client-" + serviceName,
                startTimestamp,
                duration,
                serviceName,
                "client"
        );

        // 清理TraceContext
        TraceContext.clear();
    }
}
