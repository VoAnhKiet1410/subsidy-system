package com.trocap.hoso.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HoSoRequest {

    @NotBlank(message = "Đối tượng hưởng trợ cấp không được để trống")
    private String doiTuongId;

    @NotBlank(message = "Chương trình trợ cấp không được để trống")
    private String chuongTrinhId;

    private String moTa;             // Ghi chú, mô tả bổ sung
    private Double soTienDeXuat;     // Số tiền đề xuất (tuỳ chọn)
}
