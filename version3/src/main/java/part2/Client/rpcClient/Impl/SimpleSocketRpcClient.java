package part2.Client.rpcClient.Impl;

import part2.Client.rpcClient.RpcClient;
import part2.Common.Message.RpcRequest;
import part2.Common.Message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author yl
 * @date 2025-05-05 20:28
 */
public class SimpleSocketRpcClient implements RpcClient {
    private String host;
    private int port;

    public SimpleSocketRpcClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(request);
            oos.flush();

            RpcResponse response = (RpcResponse) ois.readObject();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
