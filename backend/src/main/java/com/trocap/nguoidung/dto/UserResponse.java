package com.trocap.nguoidung.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO — không bao giờ trả password ra ngoài.
 */
@Data
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String cccd;
    private List<String> roles;
    private String status;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
