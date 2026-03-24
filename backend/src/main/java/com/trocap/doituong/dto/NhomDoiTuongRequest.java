package com.trocap.doituong.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO dùng cho cả tạo mới và cập nhật nhóm đối tượng.
 */
@Data
public class NhomDoiTuongRequest {

    @NotBlank(message = "Tên đối tượng không được để trống")
    @Size(min = 2, max = 100, message = "Tên đối tượng phải từ 2-100 ký tự")
    private String tenDoiTuong;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String moTa;
}
