package part2;

import part1.Common.pojo.User;
import part1.Common.service.UserService;
import part2.Client.proxy.ClientProxy;

/**
 * @author yl
 * @date 2025-05-05 21:35
 */
public class TestClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999, 0);
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        System.out.println("从服务端得到user = " + user);

        User yl = User.builder().id(100).userName("yl").sex(true).build();
        Integer id = proxy.insertUserId(yl);
        System.out.println("向服务端插入user的id = " + id);
    }
}
