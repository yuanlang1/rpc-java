package com.yl.Test;

import com.yl.serializer.mySerializer.Impl.ProtoStuffSerializer;
import com.yl.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author yl
 * @date 2025-05-08 12:54
 */
@Slf4j
public class ProtoStuffSerailizerTest {
    private ProtoStuffSerializer serializer = new ProtoStuffSerializer();

    @Test
    public void test() {
        User yl = User.builder().id(10).userName("yl").sex(true).build();
        byte[] serialize = serializer.serialize(yl);
        log.info("serialize = {}", serialize);

        Object deserialize = serializer.deserialize(serialize, 1);
        log.info("deserialize = {}", deserialize);
    }
}
