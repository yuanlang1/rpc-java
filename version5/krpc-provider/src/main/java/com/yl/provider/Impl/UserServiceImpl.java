package com.yl.provider.Impl;

import com.yl.pojo.User;
import com.yl.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.UUID;

/**
 * @author yl
 * @date 2025-05-04 21:57
 */
@Slf4j
public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserId(Integer id) {
        log.info("客户端查询了ID={}的用户", id);
        Random random = new Random();
        User user = User.builder()
                .userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean())
                .build();
        log.info("返回用户信息：{}", user);
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        log.info("插入数据成功，用户名={}", user.getUserName());
        return user.getId();
    }
}
