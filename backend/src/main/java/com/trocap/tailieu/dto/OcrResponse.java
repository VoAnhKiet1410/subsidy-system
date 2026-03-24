package com.trocap.tailieu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO cho kết quả OCR.
 */
@Data
@Builder
public class OcrResponse {
    private String attachmentId;         // ID tài liệu
    private String tenTaiLieu;           // Tên file gốc
    private String loaiFile;             // MIME type
    private String ketQuaOcr;            // Nội dung text trích xuất
    private LocalDateTime processedAt;   // Thời điểm xử lý
}
