package com.trocap.chitra.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Chi trả trợ cấp — ghi nhận giao dịch giải ngân cho hồ sơ đã APPROVED.
 * References:
 *   - hoSoHoTroId → applications (hồ sơ)
 */
@Document(collection = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTraTruCap {

    @Id
    private String id;

    @Indexed
    private String hoSoHoTroId;          // → ref tới applications

    private Double soTien;               // Số tiền chi trả
    private LocalDate ngayChiTra;        // Ngày chi trả
    private String phuongThuc;           // BANK_TRANSFER, CASH, MOMO, VNPAY

    @Indexed
    private String trangThai;            // PENDING, SUCCESS, FAILED, CANCELLED

    private String ghiChu;               // Ghi chú bổ sung
    private String processedBy;          // Username người xử lý

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Enum trạng thái hợp lệ
    public enum TrangThai {
        PENDING,
        SUCCESS,
        FAILED,
        CANCELLED
    }
}
