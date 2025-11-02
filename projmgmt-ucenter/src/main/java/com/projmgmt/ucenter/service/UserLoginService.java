package com.projmgmt.ucenter.service;

import com.projmgmt.ucenter.domain.User;

public interface UserLoginService {
    User register(String username, String rawPassword);
    String login(String username, String rawPassword);
    User loginAndGetUser(String username, String rawPassword);
    String generateToken(User user);
}


