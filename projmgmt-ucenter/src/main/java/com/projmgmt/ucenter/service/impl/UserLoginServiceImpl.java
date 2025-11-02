package com.projmgmt.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.projmgmt.ucenter.core.JwtUtil;
import com.projmgmt.ucenter.domain.User;
import com.projmgmt.ucenter.mapper.UserMapper;
import com.projmgmt.ucenter.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public User register(String username, String rawPassword) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getUsername, username);
        User exist = userMapper.selectOne(qw);
        if (exist != null) {
            return exist;
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(encoder.encode(rawPassword));
        userMapper.insert(u);
        return u;
    }

    @Override
    public String login(String username, String rawPassword) {
        User user = loginAndGetUser(username, rawPassword);
        if (user == null) {
            return null;
        }
        return generateToken(user);
    }

    @Override
    public User loginAndGetUser(String username, String rawPassword) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getUsername, username);
        User user = userMapper.selectOne(qw);
        if (user == null || !encoder.matches(rawPassword, user.getPassword())) {
            return null;
        }
        return user;
    }

    @Override
    public String generateToken(User user) {
        return jwtUtil.generateToken(Long.valueOf(user.getId()), user.getUsername());
    }
}


