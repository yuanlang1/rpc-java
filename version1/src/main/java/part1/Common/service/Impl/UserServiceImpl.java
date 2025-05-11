package part1.Common.service.Impl;

import part1.Common.pojo.User;
import part1.Common.service.UserService;

import java.util.Random;
import java.util.UUID;

/**
 * @author yl
 * @date 2025-05-04 21:57
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserId(Integer id) {
        System.out.println("客户端查询" + id + "的用户");
        Random random = new Random();
        User user = User.builder().userName(UUID.randomUUID().toString())
                .id(id).sex(random.nextBoolean()).build();
        return user;
    }

    @Override
    public Integer insertUserId(User user){
        System.out.println("插入数据成功" + user.getId());
        return user.getId();
    }
}
