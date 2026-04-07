package com.trocap.tailieu.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Tài liệu đính kèm của hồ sơ hỗ trợ.
 * File thực tế lưu trên disk, MongoDB chỉ lưu metadata.
 */
@Document(collection = "attachments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaiLieuDinhKem {

    @Id
    private String id;

    @Indexed
    private String hoSoHoTroId;          // → ref tới applications

    private String tenTaiLieu;           // Tên gốc file (e.g. "cccd_scan.pdf")
    private String duongDanFile;         // Đường dẫn lưu trữ trên disk
    private String loaiFile;             // MIME type: application/pdf, image/png...
    private Long kichThuoc;              // Kích thước (bytes)
    
    // Thuộc tính mới để match với danh sách requiredDocuments của chương trình
    private String loaiGiayTo;           // Ví dụ: "CCCD", "Form Mẫu Đã Điền", "Sổ Hộ Nghèo"

    private String ketQuaOcr;            // Kết quả OCR (nếu có)

    private String uploadedBy;           // Username người upload

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
