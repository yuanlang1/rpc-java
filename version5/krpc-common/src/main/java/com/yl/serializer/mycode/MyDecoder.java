package com.yl.serializer.mycode;

import com.yl.Exception.SerializeException;
import com.yl.message.MessageType;
import com.yl.serializer.mySerializer.Serializer;
import com.yl.trace.TraceContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @author yl
 * @date 2025-05-06 11:31
 */
@Slf4j
public class MyDecoder extends ByteToMessageDecoder {
    // 负责将传入得字节流解码为业务对象，并将解码后的对象添加到out中，供下一个handler使用
    // ctx为netty得ChannelHandlerContext对象
    // in为ByteBuf对象，接收到得字节流，字节数组
    // out 存储解码后得对象
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检测可读取字节数
        if (in.readableBytes() < 6) {
            return;
        }
        // 读取traceMsg消息
        // 读取traceMsg消息长度
        int traceLength = in.readInt();
        log.info("traceLength：{}", traceLength);
        // 读取消息
        byte[] traceBytes = new byte[traceLength];
        in.readBytes(traceBytes);
        log.info("traceBytes：{}", traceBytes);
        serializeTraceMsg(traceBytes);

        // 读取消息类型，Response,Request
        short messageType = in.readShort();
        System.out.println("messageType = " + messageType);
        if (messageType != MessageType.REQUEST.getCode() && messageType != MessageType.RESPONSE.getCode()) {
            log.warn("暂不支持此种数据, messageType: {}", messageType);
        }
        // 读取序列化类型
        short serializerType = in.readShort();
        System.out.println("serializerType = " + serializerType);
        // 根据序列化类型得到序列化器
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        System.out.println("serializer = " + serializer);
        if (serializer == null) {
            log.error("不存在对应得序列化器, serializerType: {}", serializerType);
            throw new SerializeException("不存在对应得序列化器, serializerType: " + serializerType);
        }
        // 读取消息长度
        int length = in.readInt();
        System.out.println("length = " + length);
        // 数据完整不
        if (in.readableBytes() < length) {
            return;
        }
        byte[] bytes = new byte[length];
        // 读取消息数据
        in.readBytes(bytes);
        log.debug("Receive bytes: {}", Arrays.toString(bytes));
        // 根据消息类型反序列化
        Object deserialize = serializer.deserialize(bytes, messageType);
        System.out.println("deserialize = " + deserialize);
        // 添加到解码后得对象列表中
        out.add(deserialize);
    }

    // 解析并存储traceMsg
    public void serializeTraceMsg(byte[] traceByte) {
        String traceMsg = new String(traceByte);
        String[] msgs = traceMsg.split(";");
        log.info("serializeTraceMsg traceId：{} spanId：{}", msgs[0], msgs[1]);
        if (!msgs[0].equals("")) {
            TraceContext.setTraceId(msgs[0]);
        }
        if (!msgs[1].equals("")) {
            TraceContext.setSpanId(msgs[1]);
        }
    }
}
