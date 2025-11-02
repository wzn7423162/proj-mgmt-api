package com.projmgmt.controller;

import com.projmgmt.common.core.domain.AjaxResult;
import com.projmgmt.domain.User;
import com.projmgmt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 *
 * @author projmgmt
 */
@RestController
@RequestMapping("/front/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 登录方法
     *
     * @param loginRequest 登录信息
     * @return 结果
     */
    @PostMapping("/loginByPd")
    public AjaxResult login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 查询用户
        User user = userService.selectUserByUserName(username);
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return AjaxResult.error("密码错误");
        }

        // 生成token (简化版本，实际应该使用JWT)
        Map<String, Object> data = new HashMap<>();
        data.put("token", "token_" + user.getId());
        data.put("userId", user.getId());
        data.put("username", user.getUsername());

        return AjaxResult.success("登录成功", data);
    }
}

