package com.trocap.hoso.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO cho API từ chối hồ sơ — bắt buộc phải có lý do.
 */
@Data
public class RejectRequest {

    @NotBlank(message = "Lý do từ chối không được để trống")
    private String lyDoTuChoi;
}
