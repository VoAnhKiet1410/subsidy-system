package com.trocap.chitra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO tạo chi trả mới.
 */
@Data
public class PaymentRequest {

    @NotBlank(message = "Hồ sơ hỗ trợ không được để trống")
    private String hoSoHoTroId;

    @NotNull(message = "Số tiền chi trả không được để trống")
    @Positive(message = "Số tiền chi trả phải lớn hơn 0")
    private Double soTien;

    @NotBlank(message = "Phương thức chi trả không được để trống")
    private String phuongThuc;       // BANK_TRANSFER, CASH, MOMO, VNPAY

    private String ghiChu;           // Ghi chú (tuỳ chọn)
}
