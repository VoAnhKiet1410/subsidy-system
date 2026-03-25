package com.trocap.chitra.controller;

import com.trocap.chitra.dto.PaymentRequest;
import com.trocap.chitra.dto.PaymentStatusRequest;
import com.trocap.chitra.model.ChiTraTruCap;
import com.trocap.chitra.service.ChiTraService;
import com.trocap.common.dto.ApiResponse;
import com.trocap.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Chi trả trợ cấp")
public class ChiTraController {

    private final ChiTraService chiTraService;

    // ─── GET danh sách (phân trang + lọc trạng thái) ────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'OFFICER')")
    @Operation(summary = "Danh sách chi trả (phân trang, lọc trạng thái)")
    public ResponseEntity<ApiResponse<PageResponse<ChiTraTruCap>>> findAll(
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (trangThai != null && !trangThai.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                    chiTraService.findByTrangThai(trangThai, pageable))));
        }
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                chiTraService.findAll(pageable))));
    }

    // ─── GET chi tiết ───────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'OFFICER')")
    @Operation(summary = "Chi tiết giao dịch chi trả")
    public ResponseEntity<ApiResponse<ChiTraTruCap>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(chiTraService.findById(id)));
    }

    // ─── GET chi tiết theo hồ sơ ────────────────────────────────────
    @GetMapping("/application/{hoSoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'OFFICER')")
    @Operation(summary = "Danh sách chi trả theo hồ sơ")
    public ResponseEntity<ApiResponse<List<ChiTraTruCap>>> findByHoSoId(@PathVariable String hoSoId) {
        return ResponseEntity.ok(ApiResponse.success(chiTraService.findByHoSoId(hoSoId)));
    }

    // ─── POST tạo chi trả (PENDING) ─────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Tạo chi trả mới (hồ sơ phải APPROVED)")
    public ResponseEntity<ApiResponse<ChiTraTruCap>> create(
            @Valid @RequestBody PaymentRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Tạo chi trả thành công",
                chiTraService.create(request, principal.getName())));
    }

    // ─── PATCH cập nhật trạng thái ──────────────────────────────────
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Cập nhật trạng thái chi trả (SUCCESS/FAILED/CANCELLED)")
    public ResponseEntity<ApiResponse<ChiTraTruCap>> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody PaymentStatusRequest request,
            Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công",
                chiTraService.updateStatus(id, request, principal.getName())));
    }
}
