package com.yl.serializer.mySerializer.Impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.yl.Exception.SerializeException;
import com.yl.serializer.mySerializer.Serializer;
import com.yl.pojo.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author yl
 * @date 2025-05-08 12:18
 */
public class KryoSerializer implements Serializer {
    private Kryo kryo;

    public KryoSerializer() {
        this.kryo = new Kryo();
        kryo.register(User.class);
    }

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("cannot serializer null object");
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             Output output = new Output(bos)) {
            kryo.writeObject(output, obj);
            return output.toBytes();
        } catch (IOException e) {
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("cannot deserializer null or empty bytes");
        }
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             Input input = new Input(bis)) {
            // 反系列化的对象类
            Class<?> clazz = getClassForMessageType(messageType);
            return kryo.readObject(input,clazz);
        } catch (Exception e) {
            throw new SerializeException("Deserialization failed");
        }
    }

    public Class<?> getClassForMessageType(int messageType) {
        if (messageType == 1) {
            return User.class;
        } else {
            throw new SerializeException("Unknown message type: " + messageType);
        }
    }

    @Override
    public int getType() {
        return 2;
    }
}
