package part1.Common.Serializer.MySerializer;

import com.alibaba.fastjson.JSONObject;
import part1.Common.Message.RpcRequest;
import part1.Common.Message.RpcResponse;

/**
 * @author yl
 * @date 2025-05-06 10:44
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        // 将对象转换为json格式的字符的数组
        byte[] bytes = JSONObject.toJSONBytes(obj);
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        // 传输的消息为request response
        switch (messageType) {
            case 0:
                try {
                    // 将字节数组转换为RpcRequest对象
                    RpcRequest request = JSONObject.parseObject(bytes, RpcRequest.class);
                    System.out.println("request = " + request);
                    // 用户存储解析后的请求参数
                    Object[] objects = new Object[request.getParams().length];
                    // 对转换后的request的params属性进行类型判断
                    for (int i = 0; i < objects.length; i++) {
                        // paramType目标参数类型，request.getParamType类型数组
                        Class<?> paramsType = request.getParamsType()[i];
                        // 不兼容，进行类型转换
                        if (!paramsType.isAssignableFrom(request.getParams()[i].getClass())) {
                            objects[i] = JSONObject.toJavaObject((JSONObject)request.getParams()[i], request.getParamsType()[i]);
                        } else {
                            objects[i] = request.getParams()[i];
                        }
                    }
                    // 转换后的参数列表 赋值回request的params
                    request.setParams(objects);
                    obj = request;
                } catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case 1:
                // 将字节数组转换为RpcResponse对象
                RpcResponse response = JSONObject.parseObject(bytes, RpcResponse.class);
                // 获取类型
                Class<?> dataType = response.getDataType();
                // 判断类型是否兼容，否则用fastjson进行类型转换 目标类型与实际返回数据类型是否兼容
                if (!dataType.isAssignableFrom(response.getData().getClass())) {
                    response.setData(JSONObject.toJavaObject((JSONObject) response.getData(), dataType));
                }
                obj = response;
                break;
            default:
                System.out.println("暂时不兼容这种消息");
                throw new RuntimeException();
        }
        return obj;

    }

    @Override
    public int getType() {
        return 1;
    }
}
