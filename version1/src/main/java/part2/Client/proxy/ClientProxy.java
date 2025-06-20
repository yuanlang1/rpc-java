package part2.Client.proxy;

import part1.Common.Message.RpcRequest;
import part1.Common.Message.RpcResponse;
import part2.Client.rpcClient.Impl.NettyRpcClient;
import part2.Client.rpcClient.Impl.SimpleSocketRpcClient;
import part2.Client.rpcClient.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author yl
 * @date 2025-05-05 21:09
 */
public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;

    public ClientProxy(String host, int port, int choose) {
        switch (choose) {
            case 0 -> rpcClient = new NettyRpcClient(host, port);
            case 1 -> rpcClient = new SimpleSocketRpcClient(host, port);
        }
    }
    public ClientProxy(String host, int port){
        rpcClient = new NettyRpcClient(host, port);
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
