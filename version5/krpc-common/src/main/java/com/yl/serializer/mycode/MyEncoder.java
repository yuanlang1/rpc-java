package com.yl.serializer.mycode;

import com.yl.message.MessageType;
import com.yl.message.RpcRequest;
import com.yl.message.RpcResponse;
import com.yl.serializer.mySerializer.Serializer;
import com.yl.trace.TraceContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author yl
 * @date 2025-05-06 11:20
 */
// 自定义Encoder
@AllArgsConstructor
@Slf4j
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;

    // netty写出数据时调用，将Java对象编码为二级制数据
    // ctx为netty提供的上下文对象 代表管道上下文，包含通道和处理器相关信息
    // msg是要编码得消息对象
    // out为netty提供得字节缓冲区
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        log.info("Encoding message of type：{}", msg.getClass());

        // 1. 写入trace消息头
        String traceMsg = TraceContext.getTraceId() + ";" + TraceContext.getSpanId();
        log.info("traceMsg：{}", traceMsg);
        byte[] traceMsgBytes = traceMsg.getBytes();
        log.info("traceMsgBytes：{}", traceMsgBytes);
        // 1.1 写入traceMsgBytes长度
        out.writeInt(traceMsgBytes.length);
        log.info("traceMsgBytes.length：{}", traceMsgBytes.length);
        // 1.2 写入traceMsgBytes
        out.writeBytes(traceMsgBytes);

        log.info("已写入traceMsgBytes");

        // 2. 写入消息类型
        // 判断消息类型，根据消息类型写入类型标识
        if (msg instanceof RpcRequest) {
            out.writeShort(MessageType.REQUEST.getCode());
        } else if (msg instanceof RpcResponse) {
            out.writeShort(MessageType.RESPONSE.getCode());
        }
        // 3. 写入序列化类型
        out.writeShort(serializer.getType());
        System.out.println("serializer.getType = " + serializer.getType());
        // 4. 将消息转换为字符数组
        byte[] serializeBytes = serializer.serialize(msg);
        System.out.println("serializeBytes = " + Arrays.toString(serializeBytes));
        // 5. 消息长度
        out.writeInt(serializeBytes.length);
        System.out.println("serializeBytes.length = " + serializeBytes.length);
        // 6. 写入字节消息内容到输出缓冲区
        out.writeBytes(serializeBytes);
    }
}
