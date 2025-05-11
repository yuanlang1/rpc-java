package part2.Client.proxy;

import part2.Client.ServiceCenter.ServiceCenter;
import part2.Client.ServiceCenter.ZKServiceCenter;
import part2.Client.retry.guavaRetry;
import part2.Client.rpcClient.Impl.NettyRpcClient;
import part2.Client.rpcClient.RpcClient;
import part2.Common.Message.RpcRequest;
import part2.Common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author yl
 * @date 2025-05-05 21:09
 */
public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;

    public ClientProxy() throws InterruptedException {
        serviceCenter = new ZKServiceCenter();
        rpcClient = new NettyRpcClient(serviceCenter);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();
        RpcResponse response;
        System.out.println("request = " + request);
        if (serviceCenter.checkRetry(request.getInterfaceName())) {
            response = new guavaRetry().sendServiceWithRetry(request, rpcClient);
        } else {
            response = rpcClient.sendRequest(request);
        }
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
