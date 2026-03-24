package com.trocap.danhgiaai.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "danh_gia_ai")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DanhGiaAi {

    @Id
    private String id;

    @Indexed
    private String hoSoId;

    private Integer diemTinCay; // 0-100
    private String mucDoRuiRo; // LOW, MEDIUM, HIGH

    private Map<String, Object> chiTietPhanTich;

    private String danhGiaBoi; // AI model or user
    private String trangThai; // PENDING, COMPLETED, FAILED

    @CreatedDate
    private LocalDateTime createdAt;
}
