package com.trocap.nguoidung.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * VaiTro (Role) document — collection "roles"
 *
 * Thiết kế RBAC với MongoDB:
 * - Collection "roles" lưu metadata: mã (ADMIN, OFFICER...), tên hiển thị, mô tả.
 * - Collection "users" lưu List<String> roles chứa maVaiTro (codes).
 * - Khi xác thực JWT, chỉ cần đọc roles[] từ user → không cần lookup/join.
 * - Khi quản lý vai trò (CRUD, gán/xóa), dùng maVaiTro làm key liên kết.
 *
 * Ưu điểm: Nhanh (không join), đơn giản, phù hợp MongoDB denormalized design.
 */
@Document(collection = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaiTro {

    @Id
    private String id;

    @Indexed(unique = true)
    private String maVaiTro;      // ADMIN, OFFICER, CITIZEN, ACCOUNTANT

    private String tenVaiTro;     // Tên hiển thị

    private String moTa;          // Mô tả vai trò

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
