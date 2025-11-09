package com.projmgmt.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.projmgmt.ucenter.core.JwtUtil;
import com.projmgmt.ucenter.domain.User;
import com.projmgmt.ucenter.mapper.UserMapper;
import com.projmgmt.ucenter.service.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    private static final Logger log = LoggerFactory.getLogger(UserLoginServiceImpl.class);

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
        String usernameIn = username;
        String passwordIn = rawPassword;
        boolean usernameHasSpace = usernameIn != null && !usernameIn.equals(usernameIn.trim());
        boolean passwordHasSpace = passwordIn != null && !passwordIn.equals(passwordIn.trim());

        // 打印密码的每个字符的 Unicode 码点
        StringBuilder hexDump = new StringBuilder();
        if (passwordIn != null) {
            for (int i = 0; i < passwordIn.length(); i++) {
                char c = passwordIn.charAt(i);
                hexDump.append(String.format("%c[U+%04X] ", c, (int)c));
            }
        }

        log.info("[login] incoming username={}, pwdLen={}, usernameHasSpace={}, passwordHasSpace={}",
                usernameIn,
                passwordIn == null ? -1 : passwordIn.length(),
                usernameHasSpace,
                passwordHasSpace);
        log.info("[login] password chars: {}", hexDump.toString());

        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getUsername, usernameIn);
        User user = userMapper.selectOne(qw);
        String dbPwd = user == null ? null : user.getPassword();
        String dbPwdPreview = dbPwd == null ? "null" : dbPwd.substring(0, Math.min(20, dbPwd.length()));
        log.info("[login] dbUserFound={}, dbPwdPrefix={}", user != null, dbPwdPreview);

        boolean matches = user != null && rawPassword != null && encoder.matches(rawPassword, dbPwd);
        String trimmed = passwordIn == null ? null : passwordIn.trim();
        boolean matchesTrimmed = user != null && trimmed != null && encoder.matches(trimmed, dbPwd);
        boolean matchesAdmin = user != null && encoder.matches("admin123", dbPwd);
        log.info("[login] bcryptMatches={}, matchesTrimmed={}, bcrypt('admin123',db)={}", matches, matchesTrimmed, matchesAdmin);
        if (!matches) {
            return null;
        }
        return user;
    }

    @Override
    public String generateToken(User user) {
        return jwtUtil.generateToken(Long.valueOf(user.getId()), user.getUsername());
    }
}


