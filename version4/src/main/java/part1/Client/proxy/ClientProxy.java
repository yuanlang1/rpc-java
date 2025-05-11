package part1.Client.proxy;

import part1.Client.ServiceCenter.ZKServiceCenter;
import part1.Client.ServiceCenter.ServiceCenter;
import part1.Client.circuitBreaker.CircuitBreaker;
import part1.Client.circuitBreaker.CircuitBreakerProvider;
import part1.Client.retry.guavaRetry;
import part1.Client.rpcClient.Impl.NettyRpcClient;
import part1.Client.rpcClient.RpcClient;
import part1.Common.Message.RpcRequest;
import part1.Common.Message.RpcResponse;

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
    private CircuitBreakerProvider circuitBreakerProvider;

    public ClientProxy() throws InterruptedException {
        serviceCenter = new ZKServiceCenter();
        rpcClient = new NettyRpcClient(serviceCenter);
        circuitBreakerProvider = new CircuitBreakerProvider();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();
        // 熔断器
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        // 判断熔断器是否通过
        if (!circuitBreaker.allowRequest()) {
            return null;
        }
        RpcResponse response;
        System.out.println("request = " + request);
        // 对白名单里面的服务进行重试
        if (serviceCenter.checkRetry(request.getInterfaceName())) {
            response = new guavaRetry().sendServiceWithRetry(request, rpcClient);
        } else {
            response = rpcClient.sendRequest(request);
        }
        if (response.getCode() == 200) {
            circuitBreaker.recordSuccess();
        }
        if (response.getCode() == 500) {
            circuitBreaker.recordFailure();
        }
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
