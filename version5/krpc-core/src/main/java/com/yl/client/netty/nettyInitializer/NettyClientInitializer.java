package com.yl.client.netty.nettyInitializer;

import com.yl.client.netty.handler.HeartbeatHandler;
import com.yl.client.netty.handler.MDCChannelHandler;
import com.yl.client.netty.handler.NettyClientHandler;
import com.yl.serializer.mycode.MyDecoder;
import com.yl.serializer.mycode.MyEncoder;
import com.yl.serializer.mySerializer.Serializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


/**
 * @author yl
 * @date 2025-05-05 20:44
 */
@Slf4j
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 初始化，每个SocketChannel都有一个独立的pipeline
        ChannelPipeline pipeline = socketChannel.pipeline();
//        // 消息格式 长度 消息体
//        // Integer.MAX_VALUE 允许的最大帧长度 0,4 长度字段的起始位置和长度  0,4 去掉长度字段后，计算实际数据的偏移量
//        pipeline.addLast(
//                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//        // 计算当前待发送消息的长度，写入到前四个字节中
//        pipeline.addLast(new LengthFieldPrepender(4));
//        // 编码器，Java序列化方式
//        pipeline.addLast(new ObjectEncoder());
//        // 解码器 将字节流解码为Java对象
//        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
//            @Override
//            public Class<?> resolve(String className) throws ClassNotFoundException {
//                return Class.forName(className);
//            }
//        }));
        // 自定义编解码器
        try {
            pipeline.addLast(new MyDecoder());
            pipeline.addLast(new MyEncoder(Serializer.getSerializerByCode(3)));
            pipeline.addLast(new NettyClientHandler());
            pipeline.addLast(new MDCChannelHandler());

            // 客户端只关注写空闲事件 如果8秒没有发送数据，则发送心跳包
            pipeline.addLast(new IdleStateHandler(0, 8, 0, TimeUnit.SECONDS));
            pipeline.addLast(new HeartbeatHandler());
            log.info("Netty client pipeline initialized with serializer type：{}", Serializer.getSerializerByCode(3).getType());
        } catch (Exception e) {
            log.error("Error initializing Netty client pipeline", e);
            throw e;
        }
    }
}
