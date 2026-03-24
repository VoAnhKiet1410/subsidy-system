package com.trocap.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDung {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @Indexed(unique = true)
    private String email;

    private String fullName;
    private String phone;
    private String address;
    private String cccd;

    private List<String> roles;

    private String status; // ACTIVE, INACTIVE

    private String avatar;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum Role {
        ADMIN,
        OFFICER,     // Cán bộ xét duyệt
        CITIZEN,     // Người dân
        ACCOUNTANT   // Kế toán
    }

    public enum Status {
        ACTIVE, INACTIVE
    }
}
