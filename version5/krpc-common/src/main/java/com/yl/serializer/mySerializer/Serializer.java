package com.yl.serializer.mySerializer;


import com.yl.serializer.mySerializer.Impl.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yl
 * @date 2025-05-06 10:26
 */
// 为对象提供序列化和反序列化的功能
// 通过静态工厂提供类型代码具体的序列化容器
public interface Serializer {
    // 把序列化对象转换为字节数组
    byte[] serialize(Object obj) throws IOException;

    // 反序列化
    Object deserialize(byte[] bytes, int messageType);

    // 返回使用的序列化器是那个
    int getType();

    // 静态工厂 根据code得到序列化器
    static Serializer getSerializerByCode(int code) {
        Map<Integer, Serializer> serializerMap = new HashMap<>();
        serializerMap.put(0, new ObjectSerializer());
        serializerMap.put(1, new JsonSerializer());
        serializerMap.put(2, new KryoSerializer());
        serializerMap.put(3, new HessianSerializer());
        serializerMap.put(4, new ProtoStuffSerializer());

        return serializerMap.get(code);
    }
}
