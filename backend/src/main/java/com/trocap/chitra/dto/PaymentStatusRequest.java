package com.trocap.chitra.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO cập nhật trạng thái chi trả.
 */
@Data
public class PaymentStatusRequest {

    @NotBlank(message = "Trạng thái không được để trống")
    private String trangThai;  // SUCCESS, FAILED, CANCELLED
}
