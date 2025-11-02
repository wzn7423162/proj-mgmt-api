package com.projmgmt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.projmgmt.domain.User;

/**
 * 用户Service接口
 *
 * @author projmgmt
 */
public interface UserService extends IService<User> {

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    User selectUserByUserName(String username);

    /**
     * 通过手机号查询用户
     *
     * @param phone 手机号
     * @return 用户对象
     */
    User selectUserByPhone(String phone);
}

