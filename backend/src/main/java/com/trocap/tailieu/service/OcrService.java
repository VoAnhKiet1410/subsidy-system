package com.trocap.tailieu.service;

import java.nio.file.Path;

/**
 * Interface OCR — dễ dàng thay engine thật sau này.
 *
 * Để đổi sang engine thật (Tesseract, Google Vision, AWS Textract...):
 * 1. Tạo class mới implements OcrService
 * 2. Đánh @Primary hoặc @Profile("production")
 * 3. Inject dependencies của engine
 *
 * Mock hiện tại: MockOcrServiceImpl
 */
public interface OcrService {

    /**
     * Trích xuất nội dung text từ file.
     *
     * @param filePath  đường dẫn tuyệt đối tới file trên disk
     * @param mimeType  loại file (application/pdf, image/png...)
     * @return          nội dung text được trích xuất
     * @throws OcrProcessingException nếu xảy ra lỗi
     */
    String extractText(Path filePath, String mimeType);

    /**
     * Kiểm tra file có hỗ trợ OCR không.
     */
    boolean supports(String mimeType);
}
