package part2.Client.netty.nettyInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import part2.Client.netty.handler.NettyClientHandler;

/**
 * @author yl
 * @date 2025-05-05 20:44
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 初始化，每个SocketChannel都有一个独立的pipeline
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 消息格式 长度 消息体
        // Integer.MAX_VALUE 允许的最大帧长度 0,4 长度字段的起始位置和长度  0,4 去掉长度字段后，计算实际数据的偏移量
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        // 计算当前待发送消息的长度，写入到前四个字节中
        pipeline.addLast(new LengthFieldPrepender(4));
        // 编码器，Java序列化方式
        pipeline.addLast(new ObjectEncoder());
        // 解码器 将字节流解码为Java对象
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));
        pipeline.addLast(new NettyClientHandler());
    }
}
