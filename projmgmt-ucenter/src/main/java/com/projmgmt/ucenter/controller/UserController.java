package com.projmgmt.ucenter.controller;

import com.projmgmt.ucenter.core.AjaxResult;
import com.projmgmt.ucenter.core.JwtUtil;
import com.projmgmt.ucenter.domain.User;
import com.projmgmt.ucenter.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/front/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取用户信息
     */
    @GetMapping("/getUserInfo")
    public AjaxResult getUserInfo(@RequestHeader(value = "FrontToken", required = false) String frontToken) {
        if (frontToken == null || !frontToken.startsWith("Bearer ")) {
            return AjaxResult.error("未登录");
        }
        String token = frontToken.substring("Bearer ".length()).trim();
        try {
            Claims claims = jwtUtil.parseToken(token);
            String userId = String.valueOf(claims.get("userId"));
            if (userId == null) {
                return AjaxResult.error("token无效");
            }
            User user = userMapper.selectById(userId);
            if (user == null) {
                return AjaxResult.error("查不到用户信息");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            // 默认头像索引，避免前端空值处理异常
            data.put("avatarIndex", "0");
            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error("token无效");
        }
    }
}


