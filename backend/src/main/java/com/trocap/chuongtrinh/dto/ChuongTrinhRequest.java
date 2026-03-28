package com.trocap.chuongtrinh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ChuongTrinhRequest {

    @NotBlank(message = "Tên chương trình không được để trống")
    @Size(min = 3, max = 200, message = "Tên chương trình phải từ 3-200 ký tự")
    private String tenChuongTrinh;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String moTa;

    @NotBlank(message = "Nguồn quỹ không được để trống")
    private String nguonQuyId;

    private Double nganSach;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate ngayKetThuc;

    private String trangThai; // UPCOMING, OPEN, CLOSED — mặc định UPCOMING

    private java.util.List<String> danhSachDoiTuong; // Danh mục đối tượng được phép
}
