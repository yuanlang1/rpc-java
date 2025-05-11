package com.yl.client.netty.handler;

import com.yl.trace.TraceContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author yl
 * @date 2025-05-10 15:24
 */
@Slf4j
public class MDCChannelHandler extends ChannelOutboundHandlerAdapter {
    public static final AttributeKey<Map<String, String>> TRACE_CONTEXT_KEY = AttributeKey.valueOf("TraceContext");

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // 从channel属性中获取Trace上下文
        Map<String, String> traceContext =
                ctx.channel().attr(TRACE_CONTEXT_KEY).get();

        if (traceContext != null) {
            // 设置到当前线程的TraceContext或MDC
            TraceContext.clone(traceContext);
            log.info("已绑定Trace上下文：{}", traceContext);
        } else {
            log.error("Trace上下文为设置！");
        }

        // 继续传递请求
        super.write(ctx, msg, promise);
    }
}
