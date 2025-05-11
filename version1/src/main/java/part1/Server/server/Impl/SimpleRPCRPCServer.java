package part1.Server.server.Impl;

import lombok.AllArgsConstructor;
import part1.Server.provider.ServiceProvider;
import part1.Server.server.RpcServer;
import part1.Server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author yl
 * @date 2025-05-04 17:22
 */
@AllArgsConstructor
public class SimpleRPCRPCServer implements RpcServer {
    private ServiceProvider serviceProvider;
    @Override
    public void start(int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器启动了");
            while (true){
                // 如果没有连接，阻塞
                Socket socket = serverSocket.accept();
                // 有链接，创建一个新的线程执行处理
                new Thread(new WorkThread(socket, serviceProvider)).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
