package part1.Client.circuitBreaker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yl
 * @date 2025-05-07 19:43
 */
public class CircuitBreakerProvider {
    // 存储每个服务的熔断器实例，key为服务名 value为该服务的熔断器
    private Map<String, CircuitBreaker> circuitBreakerMap = new HashMap<>();

    // 根据服务名获取对应的熔断器
    public synchronized CircuitBreaker getCircuitBreaker(String serviceName) {
        CircuitBreaker circuitBreaker;
        // 是否已经存在该服务的熔断器
        if (circuitBreakerMap.containsKey(serviceName)) {
            circuitBreaker = circuitBreakerMap.get(serviceName);
        } else {
            System.out.println("serviceName=" + serviceName + " 创建一个新的熔断器");
            // 创建新熔断器
            circuitBreaker = new CircuitBreaker(1, 0.5, 10000);
            circuitBreakerMap.put(serviceName, circuitBreaker);
        }
        return circuitBreaker;
    }

}
