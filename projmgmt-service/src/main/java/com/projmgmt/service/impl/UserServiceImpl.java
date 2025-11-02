package com.projmgmt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projmgmt.domain.User;
import com.projmgmt.mapper.UserMapper;
import com.projmgmt.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现
 *
 * @author projmgmt
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User selectUserByUserName(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        queryWrapper.eq(User::getDelFlag, "0");
        return this.getOne(queryWrapper);
    }

    @Override
    public User selectUserByPhone(String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        queryWrapper.eq(User::getDelFlag, "0");
        return this.getOne(queryWrapper);
    }
}

