package com.trocap.tailieu.controller;

import com.trocap.common.dto.ApiResponse;
import com.trocap.tailieu.dto.OcrResponse;
import com.trocap.tailieu.model.TaiLieuDinhKem;
import com.trocap.tailieu.service.TaiLieuService;
import com.trocap.tailieu.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Attachments", description = "Tài liệu đính kèm hồ sơ")
public class TaiLieuController {

    private final TaiLieuService taiLieuService;
    private final FileStorageService fileStorageService;

    // ─── GET serve file thực tế từ disk ─────────────────────────────
    @GetMapping("/api/files/{filename:.+}")
    @Operation(summary = "Xem/tải file tài liệu đính kèm")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path filePath = fileStorageService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String contentType = java.nio.file.Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ─── POST upload tệp chung (ví dụ Form Mẫu cho Chương Trình) ─────
    @PostMapping(value = "/api/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Upload file chung, trả về URL tải về")
    public ResponseEntity<ApiResponse<String>> uploadGenericFile(@RequestParam("file") MultipartFile file) {
        String filename = fileStorageService.store(file);
        // Trả về API endpoint để tải xuống file này
        return ResponseEntity.ok(ApiResponse.success("Upload thành công", "/api/files/" + filename));
    }

    // ─── POST upload tài liệu cho hồ sơ ────────────────────────────
    @PostMapping(value = "/api/applications/{applicationId}/attachments",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    @Operation(summary = "Upload tài liệu đính kèm cho hồ sơ")
    public ResponseEntity<ApiResponse<TaiLieuDinhKem>> upload(
            @PathVariable String applicationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "loaiGiayTo", required = false) String loaiGiayTo,
            Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Upload tài liệu thành công",
                taiLieuService.upload(applicationId, file, loaiGiayTo, principal.getName())));
    }

    // ─── GET danh sách tài liệu theo hồ sơ ─────────────────────────
    @GetMapping("/api/applications/{applicationId}/attachments")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    @Operation(summary = "Danh sách tài liệu đính kèm của hồ sơ")
    public ResponseEntity<ApiResponse<List<TaiLieuDinhKem>>> findByApplication(
            @PathVariable String applicationId, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                taiLieuService.findByHoSo(applicationId, principal.getName())));
    }

    // ─── GET chi tiết tài liệu ──────────────────────────────────────
    @GetMapping("/api/attachments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    @Operation(summary = "Chi tiết tài liệu đính kèm")
    public ResponseEntity<ApiResponse<TaiLieuDinhKem>> findById(
            @PathVariable String id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                taiLieuService.findByIdWithAccess(id, principal.getName())));
    }

    // ─── DELETE xóa tài liệu ────────────────────────────────────────
    @DeleteMapping("/api/attachments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    @Operation(summary = "Xóa tài liệu đính kèm")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id, Principal principal) {
        taiLieuService.delete(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Xóa tài liệu thành công", null));
    }

    // ─── POST chạy OCR cho tài liệu ────────────────────────────────
    @PostMapping("/api/attachments/{id}/ocr")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Chạy OCR trích xuất nội dung tài liệu")
    public ResponseEntity<ApiResponse<OcrResponse>> processOcr(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Xử lý OCR thành công",
                taiLieuService.processOcr(id)));
    }
}
