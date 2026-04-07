package com.trocap.chuongtrinh.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Chương trình trợ cấp — tham chiếu nguồn quỹ qua nguonQuyId.
 */
@Document(collection = "programs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChuongTrinhTruCap {

    @Id
    private String id;

    private String tenChuongTrinh;       // Tên chương trình
    private String moTa;                 // Mô tả chi tiết

    @Indexed
    private String nguonQuyId;           // → ref tới collection nguon_quy

    private Double nganSach;             // Ngân sách
    private Double daChi;                // Đã chi tiêu

    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;

    private String templateFormUrl;               // Đường dẫn tải file Form mẫu
    private java.util.List<String> requiredDocuments;       // Danh sách giấy tờ bắt buộc (VD: ["CCCD", "Đơn xin hỗ trợ", "Hộ nghèo"])

    @Indexed
    private String trangThai;            // UPCOMING, OPEN, CLOSED

    private String createdBy;            // Username người tạo

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Enum giới hạn giá trị hợp lệ
    public enum TrangThai {
        UPCOMING, OPEN, CLOSED
    }
}
