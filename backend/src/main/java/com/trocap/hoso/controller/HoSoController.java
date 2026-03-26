package com.trocap.hoso.controller;

import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.common.dto.ApiResponse;
import com.trocap.common.dto.PageResponse;
import com.trocap.hoso.dto.HoSoRequest;
import com.trocap.hoso.dto.RejectRequest;
import com.trocap.hoso.model.HoSoHoTro;
import com.trocap.hoso.service.HoSoService;
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
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Hồ sơ hỗ trợ")
public class HoSoController {

    private final HoSoService hoSoService;
    private final NguoiDungRepository nguoiDungRepository;
    private final com.trocap.hoso.repository.HoSoRepository hoSoRepository;

    // ─── GET tìm kiếm hồ sơ (nhiều filter, phân trang, role-aware) ──
    @GetMapping
    @Operation(summary = "Tìm kiếm hồ sơ (phân trang, lọc đa điều kiện)")
    public ResponseEntity<ApiResponse<PageResponse<HoSoHoTro>>> search(
            Principal principal,
            @RequestParam(required = false) String nguoiDungId,
            @RequestParam(required = false) String chuongTrinhTroCapId,
            @RequestParam(required = false) String doiTuongHuongTroCapId,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromNgayNopHoSo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toNgayNopHoSo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        // Validate và giới hạn pageSize để tránh memory issue
        if (size > 100) {
            size = 100;
        }
        if (size < 1) {
            size = 10;
        }

        // Parse sort param (format: "field,direction")
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction sortDir = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1])
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        var pageable = PageRequest.of(page, size, Sort.by(sortDir, sortField));

        // CITIZEN chỉ xem hồ sơ của chính mình — tự enforce nguoiDungId
        String effectiveNguoiDungId = nguoiDungId;
        NguoiDung currentUser = nguoiDungRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new com.trocap.common.exception.ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + principal.getName()));

        boolean isCitizen = currentUser.getRoles() != null
                && !currentUser.getRoles().contains("ADMIN")
                && !currentUser.getRoles().contains("OFFICER")
                && !currentUser.getRoles().contains("ACCOUNTANT");

        if (isCitizen) {
            // CITIZEN bắt buộc chỉ xem hồ sơ của mình
            effectiveNguoiDungId = currentUser.getId();
        }

        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                hoSoService.search(effectiveNguoiDungId, chuongTrinhTroCapId,
                        doiTuongHuongTroCapId, trangThai,
                        fromNgayNopHoSo, toNgayNopHoSo, pageable))));
    }

    // ─── GET thống kê hồ sơ ──────────────────────────────────────────
    @GetMapping("/stats")
    @Operation(summary = "Thống kê số lượng hồ sơ theo trạng thái")
    public ResponseEntity<ApiResponse<java.util.Map<String, Long>>> getStats() {
        var stats = new java.util.LinkedHashMap<String, Long>();
        stats.put("total", hoSoRepository.count());
        for (String status : java.util.List.of("DRAFT", "SUBMITTED", "UNDER_REVIEW", "APPROVED", "REJECTED", "PAID")) {
            stats.put(status, hoSoRepository.countByTrangThai(status));
        }
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    // ─── GET hồ sơ của CITIZEN (shortcut cho /api/applications?my) ──
    @GetMapping("/my")
    @PreAuthorize("hasRole('CITIZEN')")
    @Operation(summary = "Xem hồ sơ của tôi (CITIZEN)")
    public ResponseEntity<ApiResponse<PageResponse<HoSoHoTro>>> myApplications(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Validate và giới hạn pageSize để tránh memory issue
        if (size > 100) {
            size = 100;
        }
        if (size < 1) {
            size = 10;
        }

        NguoiDung user = nguoiDungRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new com.trocap.common.exception.ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + principal.getName()));

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                hoSoService.search(user.getId(), null, null, null, null, null, pageable))));
    }

    @GetMapping("/stats/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    @Operation(summary = "Lấy thống kê hồ sơ của tôi")
    public ResponseEntity<ApiResponse<java.util.Map<String, Long>>> getMyStats(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(hoSoService.getPersonalStats(principal.getName())));
    }

    // ─── GET chi tiết ───────────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết hồ sơ")
    public ResponseEntity<ApiResponse<HoSoHoTro>> findById(
            @PathVariable String id, Principal principal) {
        HoSoHoTro hoSo = hoSoService.findById(id);

        // CITIZEN chỉ xem hồ sơ của chính mình
        NguoiDung currentUser = nguoiDungRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new com.trocap.common.exception.ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + principal.getName()));

        boolean isCitizen = currentUser.getRoles() != null
                && !currentUser.getRoles().contains("ADMIN")
                && !currentUser.getRoles().contains("OFFICER")
                && !currentUser.getRoles().contains("ACCOUNTANT");

        if (isCitizen && !currentUser.getId().equals(hoSo.getNguoiDungId())) {
            throw new com.trocap.common.exception.BadRequestException(
                    "Bạn không có quyền xem hồ sơ này");
        }

        return ResponseEntity.ok(ApiResponse.success(hoSo));
    }

    // ─── POST tạo hồ sơ (DRAFT) ────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    @Operation(summary = "Tạo hồ sơ mới (trạng thái DRAFT)")
    public ResponseEntity<ApiResponse<HoSoHoTro>> create(
            @Valid @RequestBody HoSoRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Tạo hồ sơ thành công",
                hoSoService.create(request, principal.getName())));
    }

    // ─── PUT cập nhật hồ sơ (chỉ khi DRAFT) ────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    @Operation(summary = "Cập nhật hồ sơ (chỉ khi DRAFT)")
    public ResponseEntity<ApiResponse<HoSoHoTro>> update(
            @PathVariable String id,
            @Valid @RequestBody HoSoRequest request,
            Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật hồ sơ thành công",
                hoSoService.update(id, request, principal.getName())));
    }

    // ─── DELETE xóa hồ sơ (chỉ khi DRAFT) ──────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    @Operation(summary = "Xóa hồ sơ (chỉ khi DRAFT)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id, Principal principal) {
        hoSoService.delete(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Xóa hồ sơ thành công", null));
    }

    // ─── PATCH submit hồ sơ (DRAFT → SUBMITTED) ────────────────────
    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'CITIZEN')")
    @Operation(summary = "Nộp hồ sơ (DRAFT → SUBMITTED)")
    public ResponseEntity<ApiResponse<HoSoHoTro>> submit(@PathVariable String id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Nộp hồ sơ thành công",
                hoSoService.submit(id, principal.getName())));
    }

    // ═══════════════════════════════════════════════════════════════
    // WORKFLOW: DUYỆT / TỪ CHỐI HỒ SƠ
    // ═══════════════════════════════════════════════════════════════

    // ─── PATCH bắt đầu xét duyệt (SUBMITTED → UNDER_REVIEW) ────────
    @PatchMapping("/{id}/under-review")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Bắt đầu xét duyệt hồ sơ (SUBMITTED → UNDER_REVIEW)")
    public ResponseEntity<ApiResponse<HoSoHoTro>> startReview(
            @PathVariable String id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Chuyển sang xét duyệt thành công",
                hoSoService.startReview(id, principal.getName())));
    }

    // ─── PATCH phê duyệt (UNDER_REVIEW → APPROVED) ──────────────────
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Phê duyệt hồ sơ (UNDER_REVIEW → APPROVED)")
    public ResponseEntity<ApiResponse<HoSoHoTro>> approve(
            @PathVariable String id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Phê duyệt hồ sơ thành công",
                hoSoService.approve(id, principal.getName())));
    }

    // ─── PATCH từ chối (UNDER_REVIEW → REJECTED) ────────────────────
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Từ chối hồ sơ (UNDER_REVIEW → REJECTED)")
    public ResponseEntity<ApiResponse<HoSoHoTro>> reject(
            @PathVariable String id,
            @Valid @RequestBody RejectRequest request,
            Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Từ chối hồ sơ thành công",
                hoSoService.reject(id, request, principal.getName())));
    }
}
