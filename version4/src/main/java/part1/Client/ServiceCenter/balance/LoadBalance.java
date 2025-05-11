package part1.Client.ServiceCenter.balance;

import java.util.List;

/**
 * @author yl
 * @date 2025-05-07 10:42
 */
public interface LoadBalance {
    // 具体实现方法 返回分配的地址
    String balance(List<String> addressList);
    // 添加节点
    void addNode(String node);
    // 删除节点
    void delNode(String node);
}
