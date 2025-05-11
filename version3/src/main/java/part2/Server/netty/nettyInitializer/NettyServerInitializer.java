package part2.Server.netty.nettyInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import part2.Common.Serializer.MyCode.MyDecoder;
import part2.Common.Serializer.MyCode.MyEncoder;
import part2.Common.Serializer.MySerializer.JsonSerializer;
import part2.Server.netty.handler.NettyRPCServerHandler;
import part2.Server.provider.ServiceProvider;

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
