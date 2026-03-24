package com.trocap.thongbao.controller;

import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.common.dto.ApiResponse;
import com.trocap.common.dto.PageResponse;
import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.thongbao.dto.ThongBaoRequest;
import com.trocap.thongbao.model.ThongBao;
import com.trocap.thongbao.service.ThongBaoService;
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

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Thông báo")
public class ThongBaoController {

    private final ThongBaoService thongBaoService;
    private final NguoiDungRepository nguoiDungRepository;

    // ─── GET danh sách thông báo (của người dùng hiện tại) ──────────
    @GetMapping
    @Operation(summary = "Danh sách thông báo của tôi (lọc daDoc, phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<ThongBao>>> findMyNotifications(
            Principal principal,
            @RequestParam(required = false) Boolean daDoc,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String nguoiDungId = resolveUserId(principal);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                thongBaoService.findByNguoiDungId(nguoiDungId, daDoc, pageable))));
    }

    // ─── GET đếm chưa đọc ──────────────────────────────────────────
    @GetMapping("/unread-count")
    @Operation(summary = "Đếm thông báo chưa đọc")
    public ResponseEntity<ApiResponse<Long>> countUnread(Principal principal) {
        String nguoiDungId = resolveUserId(principal);
        return ResponseEntity.ok(ApiResponse.success(thongBaoService.countUnread(nguoiDungId)));
    }

    // ─── GET chi tiết thông báo ─────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết thông báo")
    public ResponseEntity<ApiResponse<ThongBao>> findById(
            @PathVariable String id, Principal principal) {
        ThongBao tb = thongBaoService.findById(id);
        verifyOwnership(tb, principal);
        return ResponseEntity.ok(ApiResponse.success(tb));
    }

    // ─── POST tạo thông báo thủ công (chỉ ADMIN) ───────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo thông báo thủ công (chỉ ADMIN)")
    public ResponseEntity<ApiResponse<ThongBao>> create(
            @Valid @RequestBody ThongBaoRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo thông báo thành công",
                thongBaoService.create(request)));
    }

    // ─── PATCH đánh dấu đã đọc ─────────────────────────────────────
    @PatchMapping("/{id}/read")
    @Operation(summary = "Đánh dấu thông báo đã đọc")
    public ResponseEntity<ApiResponse<ThongBao>> markRead(
            @PathVariable String id, Principal principal) {
        ThongBao tb = thongBaoService.findById(id);
        verifyOwnership(tb, principal);
        return ResponseEntity.ok(ApiResponse.success("Đánh dấu đã đọc",
                thongBaoService.markRead(id)));
    }

    // ─── PATCH đánh dấu tất cả đã đọc ──────────────────────────────
    @PatchMapping("/read-all")
    @Operation(summary = "Đánh dấu tất cả thông báo đã đọc")
    public ResponseEntity<ApiResponse<Void>> markAllRead(Principal principal) {
        String nguoiDungId = resolveUserId(principal);
        thongBaoService.markAllRead(nguoiDungId);
        return ResponseEntity.ok(ApiResponse.success("Đánh dấu tất cả đã đọc", null));
    }

    // ─── DELETE xóa thông báo ───────────────────────────────────────
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thông báo")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id, Principal principal) {
        ThongBao tb = thongBaoService.findById(id);
        verifyOwnership(tb, principal);
        thongBaoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thông báo thành công", null));
    }

    // ─── Helper: resolve userId từ username ─────────────────────────
    private String resolveUserId(Principal principal) {
        NguoiDung user = nguoiDungRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + principal.getName()));
        return user.getId();
    }

    // ─── Helper: kiểm tra quyền sở hữu thông báo ───────────────────
    private void verifyOwnership(ThongBao tb, Principal principal) {
        NguoiDung user = nguoiDungRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + principal.getName()));

        // ADMIN xem tất cả
        if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
            return;
        }

        // Người dùng chỉ xem thông báo của mình
        if (!user.getId().equals(tb.getNguoiDungId())) {
            throw new BadRequestException("Bạn không có quyền truy cập thông báo này");
        }
    }
}
