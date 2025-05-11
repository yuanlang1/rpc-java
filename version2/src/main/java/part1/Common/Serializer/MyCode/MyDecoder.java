package part1.Common.Serializer.MyCode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import part1.Common.Message.MessageType;
import part1.Common.Serializer.MySerializer.Serializer;

import java.util.Arrays;
import java.util.List;

/**
 * @author yl
 * @date 2025-05-06 11:31
 */
public class MyDecoder extends ByteToMessageDecoder {
    // 负责将传入得字节流解码为业务对象，并将解码后的对象添加到out中，供下一个handler使用
    // ctx为netty得ChannelHandlerContext对象
    // in为ByteBuf对象，接收到得字节流，字节数组
    // out 存储解码后得对象
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 消息类型，Response,Request
        short messageType = in.readShort();
        System.out.println("messageType = " + messageType);
        if (messageType != MessageType.REQUEST.getCode() && messageType != MessageType.RESPONSE.getCode()) {
            System.out.println("暂不支持此种数据");
        }
        // 读取序列化类型
        short serializerType = in.readShort();
        System.out.println("serializerType = " + serializerType);
        // 根据序列化类型得到序列化器
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        System.out.println("serializer = " + serializer);
        if (serializer == null) {
            throw new RuntimeException("不存在对应得序列化器");
        }
        // 消息长度
        int length = in.readInt();
        System.out.println("length = " + length);
        byte[] bytes = new byte[length];
        // 消息数据
        in.readBytes(bytes);
        System.out.println("bytes = " + Arrays.toString(bytes));
        // 根据消息类型反序列化
        Object deserialize = serializer.deserialize(bytes, messageType);
        System.out.println("deserialize = " + deserialize);
        // 添加到解码后得对象列表中
        out.add(deserialize);
    }
}
