package com.yl.service;

import com.yl.annotation.Retryable;
import com.yl.pojo.User;

/**
 * @author yl
 * @date 2025-05-04 21:57
 */
public interface UserService {
    @Retryable
    User getUserByUserId(Integer id);

    @Retryable
    Integer insertUserId(User user);
}
