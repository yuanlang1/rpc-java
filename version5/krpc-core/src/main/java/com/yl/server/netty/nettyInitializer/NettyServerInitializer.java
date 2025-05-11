package com.yl.server.netty.nettyInitializer;

import com.yl.serializer.mycode.MyDecoder;
import com.yl.serializer.mycode.MyEncoder;
import com.yl.serializer.mySerializer.Serializer;
import com.yl.server.netty.handler.HeartbeatHandler;
import com.yl.server.netty.handler.NettyRPCServerHandler;
import com.yl.server.provider.ServiceProvider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author yl
 * @date 2025-05-05 21:51
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 关注写事件和都读事件，如果10s内没有收到客户端的消息，触发IdleState.READER_IDLE
        pipeline.addLast(new IdleStateHandler(10, 20, 0, TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatHandler());
        // 自定义编解码器
        pipeline.addLast(new MyEncoder(Serializer.getSerializerByCode(3)));
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
    }
}
