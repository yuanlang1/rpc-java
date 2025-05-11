package part1.Common.Serializer.MyCode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import part1.Common.Message.MessageType;
import part1.Common.Message.RpcRequest;
import part1.Common.Message.RpcResponse;
import part1.Common.Serializer.MySerializer.Serializer;

import java.util.Arrays;

/**
 * @author yl
 * @date 2025-05-06 11:20
 */
// 自定义Encoder
@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;

    // netty写出数据时调用，将Java对象编码为二级制数据
    // ctx为netty提供的上下文对象 代表管道上下文，包含通道和处理器相关信息
    // msg是要编码得消息对象
    // out为netty提供得字节缓冲区
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        System.out.println("msg.getClass = " + msg.getClass());
        // 判断消息类型，根据消息类型写入类型标识
        if (msg instanceof RpcRequest) {
            out.writeShort(MessageType.REQUEST.getCode());
        } else if (msg instanceof RpcResponse) {
            out.writeShort(MessageType.RESPONSE.getCode());
        }
        // 序列化类型
        out.writeShort(serializer.getType());
        System.out.println("serializer.getType = " + serializer.getType());
        // 将消息转换为字符数组
        byte[] serializeBytes = serializer.serialize(msg);
        System.out.println("serializeBytes = " + Arrays.toString(serializeBytes));
        // 消息长度
        out.writeInt(serializeBytes.length);
        System.out.println("serializeBytes.length = " + serializeBytes.length);
        // 写入字节消息内容到输出缓冲区
        out.writeBytes(serializeBytes);
    }
}
