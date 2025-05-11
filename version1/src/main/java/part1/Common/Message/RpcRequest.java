package part1.Common.Message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yl
 * @date 2025-05-04 17:13
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    // 服务类名
    public String interfaceName;
    // 调用的方法名
    private String methodName;
    // 参数列表
    public Object[] params;
    // 参数类型
    public Class<?>[] paramsType;
}
