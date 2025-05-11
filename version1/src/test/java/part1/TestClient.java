package part1;

import part1.Client.proxy.ClientProxy;
import part1.Common.pojo.User;
import part1.Common.service.UserService;

/**
 * @author yl
 * @date 2025-05-05 16:07
 */
public class TestClient {
    public static void main(String[] args) {
        // 代理
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);
        // 为接口创建代理对象
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        System.out.println("从服务端获取到 user = " + user);

        User yl = User.builder().id(100).userName("yl").sex(true).build();
        proxy.insertUserId(yl);
        System.out.println("向服务端插入 yl = " + yl);
    }
}
