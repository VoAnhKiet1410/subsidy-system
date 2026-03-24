package com.trocap.thongbao.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Thông báo cho người dùng.
 * Hệ thống tự tạo khi hồ sơ được duyệt/từ chối/chi trả.
 * ADMIN cũng có thể tạo thủ công.
 */
@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongBao {

    @Id
    private String id;

    @Indexed
    private String nguoiDungId;          // Người nhận thông báo

    private String tieuDe;               // Tiêu đề thông báo
    private String noiDung;              // Nội dung chi tiết

    private String loai;                 // INFO, SUCCESS, WARNING, ERROR

    private String entityType;           // APPLICATION, PAYMENT, SYSTEM...
    private String entityId;             // ID của entity liên quan
    private String link;                 // Link điều hướng (tuỳ chọn)

    @Builder.Default
    private boolean daDoc = false;       // Đã đọc chưa

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum Loai {
        INFO, SUCCESS, WARNING, ERROR
    }
}
