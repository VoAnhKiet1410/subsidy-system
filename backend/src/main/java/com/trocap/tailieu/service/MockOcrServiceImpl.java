package com.trocap.tailieu.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Mock OCR implementation — trả kết quả giả lập.
 *
 * Để chuyển sang engine thật:
 * 1. Tạo TesseractOcrServiceImpl / GoogleVisionOcrServiceImpl
 * 2. Đánh @Primary trên class mới
 * 3. Class này sẽ tự động bị bỏ qua
 */
@Slf4j
@Service
public class MockOcrServiceImpl implements OcrService {

    private static final Set<String> SUPPORTED_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpg",
            "image/jpeg"
    );

    @Override
    public String extractText(Path filePath, String mimeType) {
        log.info("[MockOCR] Xử lý file: {} ({})", filePath.getFileName(), mimeType);

        // Giả lập kết quả OCR dựa trên loại file
        String fileName = filePath.getFileName().toString();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        if (mimeType != null && mimeType.startsWith("image/")) {
            return buildMockImageOcr(fileName, timestamp);
        }
        return buildMockPdfOcr(fileName, timestamp);
    }

    @Override
    public boolean supports(String mimeType) {
        return mimeType != null && SUPPORTED_TYPES.contains(mimeType.toLowerCase());
    }

    // ─── Mock data helpers ──────────────────────────────────────────

    private String buildMockImageOcr(String fileName, String timestamp) {
        return """
                [Mock OCR - Image] File: %s
                Thời gian xử lý: %s
                ---
                CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM
                Độc lập - Tự do - Hạnh phúc
                ---
                CĂN CƯỚC CÔNG DÂN
                Số: 001234567890
                Họ và tên: NGUYỄN VĂN A
                Ngày sinh: 01/01/1990
                Giới tính: Nam
                Quốc tịch: Việt Nam
                Nơi thường trú: 123 Đường ABC, Phường XYZ, Quận 1, TP.HCM
                ---
                [Lưu ý: Đây là kết quả OCR giả lập. Thay MockOcrServiceImpl bằng engine thật để có kết quả chính xác.]
                """.formatted(fileName, timestamp);
    }

    private String buildMockPdfOcr(String fileName, String timestamp) {
        return """
                [Mock OCR - PDF] File: %s
                Thời gian xử lý: %s
                Số trang: 2
                ---
                ĐƠN XIN HỖ TRỢ TRỢ CẤP XÃ HỘI
                
                Kính gửi: UBND Phường/Xã ...
                
                Tên tôi là: Nguyễn Văn A
                CCCD/CMND: 001234567890
                Địa chỉ: 123 Đường ABC, Phường XYZ, Quận 1, TP.HCM
                
                Tôi làm đơn này kính đề nghị quý cơ quan xem xét hỗ trợ trợ cấp
                theo chương trình hỗ trợ người có thu nhập thấp năm 2024.
                
                Tôi xin cam đoan những thông tin trên là đúng sự thật.
                
                Ngày ... tháng ... năm 2024
                Người làm đơn
                (Ký và ghi rõ họ tên)
                ---
                [Lưu ý: Đây là kết quả OCR giả lập. Thay MockOcrServiceImpl bằng engine thật để có kết quả chính xác.]
                """.formatted(fileName, timestamp);
    }
}
