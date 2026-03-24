package com.trocap.thongbao.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO cho ADMIN tạo thông báo thủ công.
 */
@Data
public class ThongBaoRequest {

    @NotBlank(message = "Người nhận không được để trống")
    private String nguoiDungId;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String tieuDe;

    @NotBlank(message = "Nội dung không được để trống")
    private String noiDung;

    private String loai;            // INFO, SUCCESS, WARNING, ERROR (mặc định INFO)
    private String link;            // Link tuỳ chọn
}
