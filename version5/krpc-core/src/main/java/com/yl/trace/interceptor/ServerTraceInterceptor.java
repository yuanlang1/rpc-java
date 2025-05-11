package com.yl.trace.interceptor;

import com.yl.trace.TraceContext;
import com.yl.trace.TraceIdGenerator;
import com.yl.trace.ZipkinReporter;

/**
 * @author yl
 * @date 2025-05-10 12:34
 */
public class ServerTraceInterceptor {
    // 获取和生成链路数据
    public static void beforeHandle() {
        String traceId = TraceContext.getTraceId();
        String parentSpanId = TraceContext.getParentSpanId();
        String spanId = TraceIdGenerator.generateSpanId();
        TraceContext.setTraceId(traceId);
        TraceContext.setParentSpanId(parentSpanId);
        TraceContext.setSpanId(spanId);

        // 记录服务端span
        long startTimestamp = System.currentTimeMillis();
        TraceContext.setStartTimestamp(String.valueOf(startTimestamp));
    }


    // 上传链路数据
    public static void afterHandle(String serviceName) {
        long endTimestamp = System.currentTimeMillis();
        long startTimestamp = Long.valueOf(TraceContext.getStartTimestamp());
        long duration = endTimestamp - startTimestamp;

        // 上传服务端 span
        ZipkinReporter.reportSpan(
                TraceContext.getTraceId(),
                TraceContext.getSpanId(),
                TraceContext.getParentSpanId(),
                "server-" + serviceName,
                startTimestamp,
                duration,
                serviceName,
                "server"
        );

        TraceContext.clear();
    }
}
