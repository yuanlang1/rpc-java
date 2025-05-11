package part1.Client.proxy;

import lombok.AllArgsConstructor;
import part1.Client.IOClient;
import part1.Common.Message.RpcRequest;
import part1.Common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author yl
 * @date 2025-05-05 15:26
 */
@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    private String host;
    private int port;

    // jdk动态代理，反射获取request对象，socket发生到服务器
    // 用于封装请求并处理服务端响应
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();
        System.out.println("request = " + request);
        RpcResponse response = IOClient.sendRequest(host, port, request);
        return response.getData();
    }

    public <T>T getProxy(Class<T> clazz){
        // 创建并返回代理对象
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(),  // 目标接口的类加载器
                new Class[]{clazz},                                // 代理要实现的接口列表
                this);                                             // 当前InvocationHandler实例
        return (T)o;
    }

}
