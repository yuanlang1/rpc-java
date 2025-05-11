package part1.Server.netty.nettyInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import part1.Common.Serializer.MyCode.MyDecoder;
import part1.Common.Serializer.MyCode.MyEncoder;
import part1.Server.netty.handler.NettyRPCServerHandler;
import part1.Server.provider.ServiceProvider;
import part1.Common.Serializer.MySerializer.JsonSerializer;

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
        // 自定义编解码器
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
    }
}
