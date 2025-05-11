package com.yl;

import com.yl.client.proxy.ClientProxy;
import com.yl.pojo.User;
import com.yl.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yl
 * @date 2025-05-07 21:11
 */
@Slf4j
public class ConsumerTest {
    private static final int THREAD_POOL_SIZE = 20;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy = new ClientProxy();
        UserService proxy = clientProxy.getProxy(UserService.class);
        for (int i = 0; i < 120; i++) {
            final Integer i1 = i;
            if (i % 30 == 0) {
                Thread.sleep(10000);
            }
            executorService.submit(() -> {
                try {
                    User user = proxy.getUserByUserId(i1);
                    if (user != null) {
                        log.info("从服务端得到的user={}", user);
                    } else {
                        log.warn("获取的user为null, userId={}", i1);
                    }

                    Integer id = proxy.insertUserId(User.builder().id(i1).userName("user" + i1).sex(true).build());
                    if (id != null) {
                        log.info("向服务端插入user的id={}", id);
                    } else {
                        log.warn("插入失败，返回的id为null，userId={}", i1);
                    }
                } catch (Exception e) {
                    log.error("调用服务时发生异常，userId={}", i1, e);
                }
            });
        }
        executorService.shutdown();
    }
}
