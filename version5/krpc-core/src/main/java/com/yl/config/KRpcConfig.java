package com.yl.config;

import com.yl.client.serviceCenter.balance.Impl.ConsistencyHashBalance;
import com.yl.serializer.mySerializer.Serializer;
import com.yl.server.serviceRegister.Impl.ZKServiceRegister;
import lombok.*;

/**
 * @author yl
 * @date 2025-05-07 22:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class KRpcConfig {
    //名称
    private String name = "krpc";
    //端口
    private Integer port = 9999;
    //主机名
    private String host = "localhost";
    //版本号
    private String version = "1.0.0";
    //注册中心
    private String registry = new ZKServiceRegister().toString();
    //序列化器
    private String serializer = Serializer.getSerializerByCode(1).toString();
    //负载均衡
    private String loadBalance = new ConsistencyHashBalance().toString();

}
