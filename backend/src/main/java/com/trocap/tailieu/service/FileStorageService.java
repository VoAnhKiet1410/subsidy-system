package com.trocap.tailieu.service;

import com.trocap.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Service quản lý lưu trữ file trên disk.
 * File được lưu vào thư mục cấu hình qua application.properties:
 *   app.file.upload-dir=uploads
 */
@Service
public class FileStorageService {

    private final Path uploadDir;

    // Định dạng file cho phép
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpg",
            "image/jpeg"
    );

    // Phần mở rộng cho phép
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "png", "jpg", "jpeg"
    );

    public FileStorageService(@Value("${app.file.upload-dir:uploads}") String uploadPath) {
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload: " + this.uploadDir, e);
        }
    }

    /**
     * Lưu file lên disk.
     * @return đường dẫn tương đối tới file đã lưu
     */
    public String store(MultipartFile file) {
        // Validate không rỗng
        if (file.isEmpty()) {
            throw new BadRequestException("File upload không được rỗng");
        }

        // Validate MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Định dạng file không được hỗ trợ. Chỉ chấp nhận: PDF, PNG, JPG, JPEG");
        }

        // Validate extension
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Phần mở rộng file không hợp lệ: " + extension);
        }

        // Tạo tên file duy nhất: UUID + extension
        String storedFilename = UUID.randomUUID() + "." + extension;
        Path targetPath = uploadDir.resolve(storedFilename);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file: " + e.getMessage(), e);
        }

        return storedFilename;
    }

    /**
     * Xóa file khỏi disk.
     */
    public void delete(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log warning nhưng không throw — metadata đã xóa là đủ
        }
    }

    /**
     * Lấy đường dẫn đầy đủ tới file.
     */
    public Path getFilePath(String filename) {
        return uploadDir.resolve(filename).normalize();
    }

    // ─── Helper ─────────────────────────────────────────────────────
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
