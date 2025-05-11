package part1;

import part1.Client.proxy.ClientProxy;
import part1.Common.pojo.User;
import part1.Common.service.UserService;

import java.net.Proxy;

/**
 * @author yl
 * @date 2025-05-06 12:29
 */
public class TestClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy();
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        System.out.println("从服务端得到得user = " + user.toString());

        User yl = User.builder().id(100).userName("yl").sex(true).build();
        Integer id = proxy.insertUserId(yl);
        System.out.println("向服务端插入得user的id = " + id);
    }
}
