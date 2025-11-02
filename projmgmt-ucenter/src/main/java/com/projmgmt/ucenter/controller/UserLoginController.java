package com.projmgmt.ucenter.controller;

import com.projmgmt.ucenter.core.AjaxResult;
import com.projmgmt.ucenter.domain.User;
import com.projmgmt.ucenter.service.UserLoginService;
import com.projmgmt.ucenter.vo.LoginUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/front/login")
public class UserLoginController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("/loginByPd")
    public AjaxResult loginByPd(@RequestBody LoginUserRequest req) {
        String username = req.getUsername() != null ? req.getUsername() : req.getPhone();
        User user = userLoginService.loginAndGetUser(username, req.getPassword());
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


