package com.trocap.nguoidung.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VaiTroRequest {
    @NotBlank(message = "Mã vai trò không được để trống")
    @Pattern(regexp = "^[A-Z_]{2,30}$", message = "Mã vai trò phải viết HOA, chỉ chứa A-Z và _, 2-30 ký tự")
    private String maVaiTro;

    @NotBlank(message = "Tên vai trò không được để trống")
    private String tenVaiTro;

    private String moTa;
}
