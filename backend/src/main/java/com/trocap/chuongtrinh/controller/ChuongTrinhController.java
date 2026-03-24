package com.trocap.chuongtrinh.controller;

import com.trocap.chuongtrinh.dto.ChuongTrinhRequest;
import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import com.trocap.chuongtrinh.service.ChuongTrinhService;
import com.trocap.common.dto.ApiResponse;
import com.trocap.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
@Tag(name = "Programs", description = "Quản lý chương trình trợ cấp")
public class ChuongTrinhController {

    private final ChuongTrinhService chuongTrinhService;

    // ─── GET danh sách (phân trang + search + lọc trạng thái + khoảng ngày) ──
    @GetMapping
    @Operation(summary = "Danh sách chương trình (phân trang, lọc, tìm kiếm)")
    public ResponseEntity<ApiResponse<PageResponse<ChuongTrinhTruCap>>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Ưu tiên: search > trangThai > khoảng ngày > tất cả
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                    chuongTrinhService.search(search, pageable))));
        }
        if (trangThai != null && !trangThai.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                    chuongTrinhService.findByTrangThai(trangThai, pageable))));
        }
        if (fromDate != null && toDate != null) {
            return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                    chuongTrinhService.findByDateRange(fromDate, toDate, pageable))));
        }

        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                chuongTrinhService.findAll(pageable))));
    }

    // ─── GET chi tiết ───────────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết chương trình")
    public ResponseEntity<ApiResponse<ChuongTrinhTruCap>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(chuongTrinhService.findById(id)));
    }

    // ─── POST tạo mới ───────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Tạo chương trình mới")
    public ResponseEntity<ApiResponse<ChuongTrinhTruCap>> create(
            @Valid @RequestBody ChuongTrinhRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Tạo chương trình thành công",
                chuongTrinhService.create(request, principal.getName())));
    }

    // ─── PUT cập nhật ───────────────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Cập nhật chương trình")
    public ResponseEntity<ApiResponse<ChuongTrinhTruCap>> update(
            @PathVariable String id, @Valid @RequestBody ChuongTrinhRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công",
                chuongTrinhService.update(id, request)));
    }

    // ─── DELETE xóa ─────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Xóa chương trình")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        chuongTrinhService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa chương trình thành công", null));
    }
}
