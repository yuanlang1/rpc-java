package com.yl.client.rpcClient.Impl;

import com.yl.client.netty.handler.MDCChannelHandler;
import com.yl.client.netty.nettyInitializer.NettyClientInitializer;
import com.yl.client.rpcClient.RpcClient;
import com.yl.message.RpcRequest;
import com.yl.message.RpcResponse;
import com.yl.trace.TraceContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author yl
 * @date 2025-05-05 20:54
 */
@Slf4j
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;   // 启动客户端的对象 负责连接配置
    private static final EventLoopGroup eventLoopGroup; // 线程池 NIO
    private InetSocketAddress address;

    public NettyRpcClient(InetSocketAddress address) {
        this.address = address;
    }

    // 初始化客户端
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());  // 自定义消息处理逻辑
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        Map<String, String> mdcContextMap = TraceContext.getCopy();
        log.info("mdcContextMap：{}", mdcContextMap);
        // 从注册中心获取host, port
        if (address == null) {
            log.error("服务发现失败，返回的地址为 null");
            return RpcResponse.fail("服务发现失败，地址为 null");
        }
        String host = address.getHostName();
        int port = address.getPort();
        log.info("host：{} port：{}", host, port);
        try {
            // 创建一个channelFuture， 代表一个操作事件，sync阻塞知道connect完成
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            // 连接单位 相当余channel
            Channel channel = channelFuture.channel();
            // 将当前Trace上下文保存到Channel属性
            channel.attr(MDCChannelHandler.TRACE_CONTEXT_KEY).set(mdcContextMap);

            log.info("发送数据");
            // 发送数据
            channel.writeAndFlush(request);
            // sync()阻塞获取结果
            channel.closeFuture().sync();
            log.info("阻塞获取结果");
            // 得到别名
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            // 找到特定别名下的channel中的内容
            RpcResponse response = channel.attr(key).get();
            log.info("response：{}", response);
            if (response == null) {
                log.error("服务响应为空，可能是请求失败或超时");
                return RpcResponse.fail("服务响应为空");
            }
            log.info("收到响应：{}", response);
            return response;
        } catch (InterruptedException e) {
            log.error("请求被中断，发送请求失败：{}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("发送请求时异常：{}", e.getMessage());
        } finally {

        }
        return RpcResponse.fail("请求失败");
    }

    public void close() {
        try {
            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully().sync();
            }
        } catch (InterruptedException e) {
            log.error("关闭 Netty 资源时发生异常：{}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
