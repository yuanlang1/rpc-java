package part1.Server.server;

import java.io.IOException;

/**
 * @author yl
 * @date 2025-05-04 17:20
 */
public interface RpcServer {
    // 开始监听
    void start(int port) throws IOException;
    void stop();
}
