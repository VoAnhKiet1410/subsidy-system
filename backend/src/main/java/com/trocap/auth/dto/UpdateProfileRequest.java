package com.trocap.auth.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String cccd;
    private String ngaySinh;
}
