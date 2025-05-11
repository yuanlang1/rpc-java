package com.yl.message;

import io.protostuff.Rpc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yl
 * @date 2025-05-04 21:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    // 状态信息
    private int code;
    private String message;
    // 更新：加入传输数据的类型，以便在自定义序列化器中解析
    private Class<?> dataType;
    // 具体数据
    private Object data;


    // 构造成功信息
    public static RpcResponse success(Object data) {
        return RpcResponse.builder().code(200).dataType(data.getClass()).data(data).build();
    }

    // 构造失败信息
    public static RpcResponse fail(String msg) {
        return RpcResponse.builder().code(500).message(msg).build();
    }

}

