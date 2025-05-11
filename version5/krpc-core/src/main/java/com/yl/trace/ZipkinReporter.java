package com.yl.trace;

import lombok.extern.slf4j.Slf4j;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * @author yl
 * @date 2025-05-10 12:13
 */
@Slf4j
public class ZipkinReporter {
    // Zipkin服务器地址
    private static final String ZIPKIN_URL = "http://localhost:9412/api/v2/spans";
    private static final AsyncReporter<Span> reporter;

    static {
        // Zipkin上传器
        OkHttpSender sender = OkHttpSender.create(ZIPKIN_URL);
        reporter = AsyncReporter.create(sender);
    }

    public static void reportSpan(String traceId, String spanId, String parentId,
                                  String name, long startTimestamp, long duration,
                                  String serviceName, String type) {
        Span span = Span.newBuilder()
                .traceId(traceId)
                .id(spanId)
                .parentId(parentId)
                .name(name)
                .timestamp(startTimestamp * 1000)   // 微秒
                .duration(duration * 1000)
                .putTag("service", serviceName)
                .putTag("type", type)
                .build();

        reporter.report(span);
        log.info("当前traceId：{}正在上报日志-----", traceId);
    }

    public static void close() {
        reporter.close();
    }
}
