package part3.Client.netty.nettyInitializer.Client.proxy;

import part1.Common.Message.RpcRequest;
import part1.Common.Message.RpcResponse;
import part3.Client.netty.nettyInitializer.Client.rpcClient.Impl.NettyRpcClient;
import part3.Client.netty.nettyInitializer.Client.rpcClient.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author yl
 * @date 2025-05-05 21:09
 */
public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;

    public ClientProxy() {
        rpcClient = new NettyRpcClient();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();
        RpcResponse response = rpcClient.sendRequest(request);
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
