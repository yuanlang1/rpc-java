package part1.Server.work;

import lombok.AllArgsConstructor;
import part1.Common.Message.RpcRequest;
import part1.Common.Message.RpcResponse;
import part1.Server.provider.ServiceProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author yl
 * @date 2025-05-04 21:07
 */
@AllArgsConstructor
public class WorkThread implements Runnable{
    public Socket socket;
    public ServiceProvider serviceProvider;
    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            // 读取客户端传过来的request
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            // 反射调用方法获取返回值
            RpcResponse response = getResponse(rpcRequest);
            // 向客户端写入response
            oos.writeObject(response);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private RpcResponse getResponse(RpcRequest rpcRequest){
        // 服务名
        String interfaceName = rpcRequest.getInterfaceName();
        // 得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);

        // 反射调用方法
        Method method = null;
        try {
            // 获取方法对象
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            // 通过反射调用方法
            Object invoke = method.invoke(service, rpcRequest.getParams());
            // 封装响应对象
            return RpcResponse.success(invoke);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RpcResponse.fail();
        }
    }
}
