package com.yl.client.rpcClient;

import com.yl.message.RpcRequest;
import com.yl.message.RpcResponse;

/**
 * @author yl
 * @date 2025-05-05 20:26
 */
public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);

    void close();
}
