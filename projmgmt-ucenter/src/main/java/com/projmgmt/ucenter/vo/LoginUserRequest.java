package com.projmgmt.ucenter.vo;

import lombok.Data;

@Data
public class LoginUserRequest {
    private String username;
    private String password;
    // 为兼容保留字段
    private String phone;
    private String smsCode;
    private String uuid;
    private Long agentId;
    private Long promoteId;
    private String utm_source;
    private Long userId;
    private String userKey;
}


