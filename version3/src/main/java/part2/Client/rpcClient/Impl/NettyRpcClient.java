package part2.Client.rpcClient.Impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import part2.Client.ServiceCenter.ServiceCenter;
import part2.Client.ServiceCenter.ZKServiceCenter;
import part2.Client.netty.nettyInitializer.NettyClientInitializer;
import part2.Client.rpcClient.RpcClient;
import part2.Common.Message.RpcRequest;
import part2.Common.Message.RpcResponse;

import java.net.InetSocketAddress;

/**
 * @author yl
 * @date 2025-05-05 20:54
 */
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;   // 启动客户端的对象 负责连接配置
    private static final EventLoopGroup eventLoopGroup; // 线程池 NIO
    private ServiceCenter serviceCenter;

    public NettyRpcClient(ServiceCenter serviceCenter) throws InterruptedException {
        this.serviceCenter = serviceCenter;
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
        InetSocketAddress address = serviceCenter.serviceDiscovery(request.getInterfaceName());
        String host = address.getHostName();
        int port = address.getPort();
        try {
            // 创建一个channelFuture， 代表一个操作事件，sync阻塞知道connect完成
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            // 连接单位 相当余channel
            Channel channel = channelFuture.channel();
            // 发送数据
            channel.writeAndFlush(request);
            // sync()阻塞获取结果
            channel.closeFuture().sync();
            // 得到别名
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            // 找到特定别名下的channel中的内容
            RpcResponse response = channel.attr(key).get();
            System.out.println("response = " + response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
