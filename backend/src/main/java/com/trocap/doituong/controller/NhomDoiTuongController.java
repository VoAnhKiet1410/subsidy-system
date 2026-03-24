package com.trocap.doituong.controller;

import com.trocap.common.dto.ApiResponse;
import com.trocap.common.dto.PageResponse;
import com.trocap.doituong.dto.NhomDoiTuongRequest;
import com.trocap.doituong.model.NhomDoiTuong;
import com.trocap.doituong.service.NhomDoiTuongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beneficiary-groups")
@RequiredArgsConstructor
@Tag(name = "Beneficiary Groups", description = "Quản lý nhóm đối tượng hưởng trợ cấp")
public class NhomDoiTuongController {

    private final NhomDoiTuongService nhomDoiTuongService;

    // ─── GET danh sách (phân trang + tìm kiếm) ─────────────────────
    @GetMapping
    @Operation(summary = "Danh sách nhóm đối tượng (có phân trang, tìm kiếm)")
    public ResponseEntity<ApiResponse<PageResponse<NhomDoiTuong>>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Nếu có search → tìm theo tên, không → lấy tất cả
        var result = (search != null && !search.isBlank())
                ? nhomDoiTuongService.search(search, pageable)
                : nhomDoiTuongService.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(result)));
    }

    // ─── GET chi tiết ───────────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết nhóm đối tượng")
    public ResponseEntity<ApiResponse<NhomDoiTuong>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(nhomDoiTuongService.findById(id)));
    }

    // ─── POST tạo mới (ADMIN hoặc OFFICER) ─────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Tạo nhóm đối tượng mới")
    public ResponseEntity<ApiResponse<NhomDoiTuong>> create(@Valid @RequestBody NhomDoiTuongRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo nhóm đối tượng thành công",
                nhomDoiTuongService.create(request)));
    }

    // ─── PUT cập nhật (ADMIN hoặc OFFICER) ──────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Cập nhật nhóm đối tượng")
    public ResponseEntity<ApiResponse<NhomDoiTuong>> update(@PathVariable String id,
                                                             @Valid @RequestBody NhomDoiTuongRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công",
                nhomDoiTuongService.update(id, request)));
    }

    // ─── DELETE xóa (ADMIN hoặc OFFICER) ────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Xóa nhóm đối tượng")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        nhomDoiTuongService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa nhóm đối tượng thành công", null));
    }
}
