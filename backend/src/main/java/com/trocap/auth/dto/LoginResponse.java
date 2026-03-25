package com.trocap.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String cccd;
    private String ngaySinh;
    private List<String> roles;
}
