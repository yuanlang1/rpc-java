package part2.Common.service;

import part2.Common.pojo.User;

/**
 * @author yl
 * @date 2025-05-04 21:57
 */
public interface UserService {
    User getUserByUserId(Integer id);

    Integer insertUserId(User user);
}
