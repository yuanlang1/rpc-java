package part1.Client;

import part1.Common.Message.RpcRequest;
import part1.Common.Message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author yl
 * @date 2025-05-04 17:10
 */
public class IOClient {
    public static RpcResponse sendRequest(String host, int prot, RpcRequest request){
        try {
            Socket socket = new Socket(host, prot);

            // 将对象序列化为字节流发给服务端   输出流
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            // 接收并反序列化对象      输入流
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            // 将request序列化后，通过输出流发给服务端
            oos.writeObject(request);
            // 刷新输出流
            oos.flush();

            // 从输入流得到序列化对象，再反序列化
            RpcResponse response = (RpcResponse) ois.readObject();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
