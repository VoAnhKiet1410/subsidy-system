package com.trocap.auditlog.controller;

import com.trocap.auditlog.model.AuditLog;
import com.trocap.auditlog.repository.AuditLogRepository;
import com.trocap.common.dto.ApiResponse;
import com.trocap.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
@Tag(name = "Audit Logs", description = "Nhật ký hoạt động")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    @Operation(summary = "Lấy danh sách nhật ký")
    public ResponseEntity<ApiResponse<PageResponse<AuditLog>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                auditLogRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))))));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm nhật ký")
    public ResponseEntity<ApiResponse<PageResponse<AuditLog>>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                auditLogRepository.findByUsernameContainingIgnoreCaseOrActionContainingIgnoreCase(
                        keyword, keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))))));
    }

    @PostMapping
    @Operation(summary = "Ghi nhật ký")
    public ResponseEntity<ApiResponse<AuditLog>> create(@RequestBody AuditLog log) {
        return ResponseEntity.ok(ApiResponse.success(auditLogRepository.save(log)));
    }
}
