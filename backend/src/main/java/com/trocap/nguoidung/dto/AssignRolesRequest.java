package com.trocap.nguoidung.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssignRolesRequest {
    @NotEmpty(message = "Danh sách vai trò không được rỗng")
    private List<String> roleCodes; // e.g. ["OFFICER", "ACCOUNTANT"]
}
