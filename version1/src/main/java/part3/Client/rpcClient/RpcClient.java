package part3.Client.rpcClient;

import part1.Common.Message.RpcRequest;
import part1.Common.Message.RpcResponse;

/**
 * @author yl
 * @date 2025-05-05 20:26
 */
public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
