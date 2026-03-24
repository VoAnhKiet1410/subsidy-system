package com.trocap.nguoidung.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String phone;
    private String address;
    private String cccd;
    private String status; // ACTIVE, INACTIVE
}
