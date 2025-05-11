package com.yl.Test;

import com.yl.serializer.mySerializer.Impl.HessianSerializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author yl
 * @date 2025-05-08 12:10
 */
@Slf4j
public class HessionSerializerTest {
    private HessianSerializer serializer = new HessianSerializer();

    @Test
    public void Test(){
        String s = "hello yl";

        byte[] serialize = serializer.serialize(s);
        log.info("serialize：{}", serialize);

        Object deserialized = serializer.deserialize(serialize, 3);
        log.info("deserialize: {}", deserialized);
    }
}
