package com.projmgmt.ucenter.controller;

import com.projmgmt.ucenter.core.AjaxResult;
import com.projmgmt.ucenter.domain.User;
import com.projmgmt.ucenter.service.UserLoginService;
import com.projmgmt.ucenter.vo.LoginUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/front/login")
public class UserLoginController {

    private static final Logger log = LoggerFactory.getLogger(UserLoginController.class);

    @Autowired
    private UserLoginService userLoginService;

    @GetMapping("/genHash")
    public AjaxResult genHash() {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String hash = encoder.encode("admin123");
        boolean verify = encoder.matches("admin123", hash);
        Map<String, Object> result = new HashMap<>();
        result.put("password", "admin123");
        result.put("hash", hash);
        result.put("verify", verify);
        return AjaxResult.success(result);
    }

    @PostMapping("/loginByPd")
    public AjaxResult loginByPd(@RequestBody LoginUserRequest req) {
        String inUsername = req.getUsername();
        String inPhone = req.getPhone();
        String pwd = req.getPassword();
        boolean usernameHasSpace = inUsername != null && !inUsername.equals(inUsername.trim());
        boolean phoneHasSpace = inPhone != null && !inPhone.equals(inPhone.trim());
        boolean pwdHasSpace = pwd != null && !pwd.equals(pwd.trim());
        log.info("[loginByPd] in username={}, phone={}, pwdLen={}, usernameHasSpace={}, phoneHasSpace={}, pwdHasSpace={}",
                inUsername, inPhone, pwd == null ? -1 : pwd.length(), usernameHasSpace, phoneHasSpace, pwdHasSpace);
        String username = inUsername != null ? inUsername.trim() : (inPhone == null ? null : inPhone.trim());
        String password = pwd == null ? null : pwd.trim();
        User user = userLoginService.loginAndGetUser(username, password);
        if (user == null) {
            return AjaxResult.error("用户名或密码错误");
        }
        String token = userLoginService.generateToken(user);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        return AjaxResult.success(data);
    }
}


