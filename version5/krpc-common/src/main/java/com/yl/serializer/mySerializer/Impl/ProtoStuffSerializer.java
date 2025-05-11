package com.yl.serializer.mySerializer.Impl;

import com.yl.Exception.SerializeException;
import com.yl.serializer.mySerializer.Serializer;
import com.yl.pojo.User;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yl
 * @date 2025-05-08 12:43
 */
@Slf4j
public class ProtoStuffSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("cannot serializer null object");
        }

        Schema schema = RuntimeSchema.getSchema(obj.getClass());

        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        byte[] bytes;
        try {
            bytes = ProtobufIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("cannot deserializer null or empty bytes");
        }

        Class<?> clazz = getClassForMessageType(messageType);
        Schema schema = RuntimeSchema.getSchema(clazz);

        Object obj;

        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new SerializeException("Deserialization falied due to reflection issues");
        }

        ProtobufIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    @Override
    public int getType() {
        return 4;
    }

    public Class<?> getClassForMessageType(int messageType) {
        if (messageType == 1) {
            return User.class;
        } else {
            throw new SerializeException("Unknown message type: " + messageType);
        }
    }
}
