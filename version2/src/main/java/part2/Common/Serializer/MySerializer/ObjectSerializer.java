package part2.Common.Serializer.MySerializer;

import java.io.*;

/**
 * @author yl
 * @date 2025-05-06 10:33
 */
public class ObjectSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = null;
        // 创建一个内存中的输出流，用于存储序列化后的字节数据
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // 将对象转换为二进制数据，写入字节缓冲区
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            // 把对象写入输出流中，触发序列化
            oos.writeObject(obj);
            oos.flush();
            // 将字节缓冲区的内容转换为字节数组
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        // 把字节数组包装成一个字节输入流
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            // 包装一个ByteArrayInputStream对象
            ObjectInputStream ois = new ObjectInputStream(bis);
            // 从ois中读取到序列化对象，反序列化为Java对象
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public int getType() {
        return 0;
    }
}
