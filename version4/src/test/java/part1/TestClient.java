package part1;

import part1.Client.proxy.ClientProxy;
import part1.Common.pojo.User;
import part1.Common.service.UserService;

/**
 * @author yl
 * @date 2025-05-07 19:59
 */
public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy = new ClientProxy();
        UserService proxy = clientProxy.getProxy(UserService.class);
        for (int i = 0; i < 120; i++) {
            Integer i1 = i;
            if (i % 30 == 0) {
                Thread.sleep(10000);
            }
            new Thread(() -> {
                try {
                    User user = proxy.getUserByUserId(i1);
                    System.out.println("从服务端得到的user = " + user);
                    Integer userId = proxy.insertUserId(User.builder().id(i1).userName("User" + i1).sex(true).build());
                    System.out.println("向服务端插入user的userId = " + userId);
                } catch (NullPointerException e) {
                    System.out.println("user为空");
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
