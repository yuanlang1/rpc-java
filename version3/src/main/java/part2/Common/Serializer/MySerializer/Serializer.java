package part2.Common.Serializer.MySerializer;


/**
 * @author yl
 * @date 2025-05-06 10:26
 */
// 为对象提供序列化和反序列化的功能
// 通过静态工厂提供类型代码具体的序列化容器
public interface Serializer {
    // 把序列化对象转换为字节数组
    byte[] serialize(Object obj);

    // 反序列化
    Object deserialize(byte[] bytes, int messageType);

    // 返回使用的序列化器是那个
    int getType();

    // 静态工厂 根据code得到序列化器
    static Serializer getSerializerByCode(int code) {
        switch (code) {
            case 0:
                return new ObjectSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
