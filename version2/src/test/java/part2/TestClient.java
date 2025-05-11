package part2;

import part2.Common.pojo.User;
import part2.Common.service.UserService;
import part2.Client.proxy.ClientProxy;

/**
 * @author yl
 * @date 2025-05-06 19:52
 */
public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy = new ClientProxy();
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        System.out.println("从服务端得到的user = " + user);

        User yl = User.builder().id(100).userName("yl").sex(true).build();
        Integer id = proxy.insertUserId(yl);
        System.out.println("向服务端插入user的id = " + id);
    }
}
