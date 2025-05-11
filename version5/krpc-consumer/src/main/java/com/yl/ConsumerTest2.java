package com.yl;

import com.yl.client.proxy.ClientProxy;
import com.yl.pojo.User;
import com.yl.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yl
 * @date 2025-05-10 20:51
 */
@Slf4j
public class ConsumerTest2 {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy = new ClientProxy();
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        if (user != null) {
            log.info("从服务端得到的user={}", user);
        } else {
            log.warn("获取的user为null, userId={}", 1);
        }

        Integer id = proxy.insertUserId(User.builder().id(1).userName("user" + 1).sex(true).build());
        if (id != null) {
            log.info("向服务端插入user的id={}", id);
        } else {
            log.warn("插入失败，返回的id为null，userId={}", 1);
        }

        clientProxy.close();
    }
}
