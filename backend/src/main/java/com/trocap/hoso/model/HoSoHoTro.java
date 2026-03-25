package com.trocap.hoso.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hồ sơ hỗ trợ — CITIZEN nộp hồ sơ xin trợ cấp.
 * References:
 *   - nguoiDungId      → users (người nộp)
 *   - doiTuongId       → beneficiaries (đối tượng hưởng trợ cấp)
 *   - chuongTrinhId    → programs (chương trình trợ cấp)
 */
@Document(collection = "applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoSoHoTro {

    @Id
    private String id;

    private String maHoSo;                // Mã hồ sơ tuần tự: HS-0001, HS-0002...

    @Indexed
    private String nguoiDungId;           // → ref tới users (người nộp)

    @Indexed
    private String doiTuongId;            // → ref tới beneficiaries

    @Indexed
    private String chuongTrinhId;         // → ref tới programs

    private LocalDate ngayNopHoSo;        // Ngày nộp hồ sơ (set khi submit)

    @Indexed
    private String trangThai;             // DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PAID

    private String lyDoTuChoi;            // Lý do từ chối (nếu REJECTED)

    private String moTa;                  // Ghi chú, mô tả
    private Double soTienDeXuat;          // Số tiền đề xuất

    private String reviewedBy;            // Người xét duyệt
    private String approvedBy;            // Người phê duyệt
    private LocalDateTime approvedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Enum trạng thái hợp lệ
    public enum TrangThai {
        DRAFT,
        SUBMITTED,
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        PAID
    }
}
