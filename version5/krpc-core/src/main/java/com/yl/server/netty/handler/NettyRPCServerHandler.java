package com.yl.server.netty.handler;

import com.yl.message.RequestType;
import com.yl.message.RpcRequest;
import com.yl.message.RpcResponse;
import com.yl.server.provider.ServiceProvider;
import com.yl.server.ratelimit.RateLimit;
import com.yl.trace.interceptor.ServerTraceInterceptor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author yl
 * @date 2025-05-05 21:39
 */
@Slf4j
@AllArgsConstructor
public class NettyRPCServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private ServiceProvider serviceProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        if (request == null) {
            log.error("接收到非法请求，RpcRequest为空");
            return;
        }
        log.info("request：{}", request);

        if (request.getType() == RequestType.HEARTBEAT) {
            log.info("接收到来自用户的心跳包");
            return;
        }

        if (request.getType() == RequestType.NORMAL) {
            // trace记录
            ServerTraceInterceptor.beforeHandle();

            RpcResponse response = getResponse(request);
            // 上报trace
            ServerTraceInterceptor.afterHandle(request.getMethodName());

            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理请求时发生异常", cause);
        ctx.close();
    }

    public RpcResponse getResponse(RpcRequest rpcRequest) {
        String interfaceName = rpcRequest.getInterfaceName();
        // 接口限流降级
        RateLimit rateLimit = serviceProvider.getRateLimitProvider().getRateLimit(interfaceName);
        if (!rateLimit.getToken()) {
            log.warn("服务限流，接口：{}", interfaceName);
            return RpcResponse.fail("服务限流，接口 " + interfaceName + "当前无法处理请求。请稍后重试。");
        }
        Object service = serviceProvider.getService(interfaceName);
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            Object invoke = method.invoke(service, rpcRequest.getParams());
            return RpcResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("方法执行错误，接口：{}，方法：{}", interfaceName, rpcRequest.getMethodName(), e);
            return RpcResponse.fail("方法执行错误");
        }
    }
}
