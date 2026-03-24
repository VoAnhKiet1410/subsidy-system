package com.trocap.tailieu.controller;

import com.trocap.common.dto.ApiResponse;
import com.trocap.tailieu.dto.OcrResponse;
import com.trocap.tailieu.model.TaiLieuDinhKem;
import com.trocap.tailieu.service.TaiLieuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Attachments", description = "Tài liệu đính kèm hồ sơ")
public class TaiLieuController {

    private final TaiLieuService taiLieuService;

    // ─── POST upload tài liệu cho hồ sơ ────────────────────────────
    @PostMapping(value = "/api/applications/{applicationId}/attachments",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    @Operation(summary = "Upload tài liệu đính kèm cho hồ sơ")
    public ResponseEntity<ApiResponse<TaiLieuDinhKem>> upload(
            @PathVariable String applicationId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Upload tài liệu thành công",
                taiLieuService.upload(applicationId, file, principal.getName())));
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
