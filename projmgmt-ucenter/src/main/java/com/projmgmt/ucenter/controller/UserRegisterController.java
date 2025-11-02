package com.projmgmt.ucenter.controller;

import com.projmgmt.ucenter.core.AjaxResult;
import com.projmgmt.ucenter.domain.User;
import com.projmgmt.ucenter.service.UserLoginService;
import com.projmgmt.ucenter.vo.LoginUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/front/register")
public class UserRegisterController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("/registerByPd")
    public AjaxResult registerByPd(@RequestBody LoginUserRequest req) {
        String username = req.getUsername() != null ? req.getUsername() : req.getPhone();
        User u = userLoginService.register(username, req.getPassword());
        return AjaxResult.success();
    }
}


