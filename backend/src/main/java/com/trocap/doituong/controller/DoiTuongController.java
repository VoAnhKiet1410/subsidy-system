package com.trocap.doituong.controller;

import com.trocap.common.dto.ApiResponse;
import com.trocap.common.dto.PageResponse;
import com.trocap.doituong.model.DoiTuongHuongTruCap;
import com.trocap.doituong.service.DoiTuongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
@Tag(name = "Beneficiaries", description = "Đối tượng hưởng trợ cấp")
public class DoiTuongController {

    private final DoiTuongService doiTuongService;

    @GetMapping
    @Operation(summary = "Lấy danh sách đối tượng")
    public ResponseEntity<ApiResponse<PageResponse<DoiTuongHuongTruCap>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                doiTuongService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết đối tượng")
    public ResponseEntity<ApiResponse<DoiTuongHuongTruCap>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(doiTuongService.findById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm đối tượng theo tên")
    public ResponseEntity<ApiResponse<PageResponse<DoiTuongHuongTruCap>>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                doiTuongService.search(keyword, PageRequest.of(page, size)))));
    }

    @GetMapping("/filter")
    @Operation(summary = "Lọc đối tượng theo category hoặc status")
    public ResponseEntity<ApiResponse<PageResponse<DoiTuongHuongTruCap>>> filter(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (category != null) {
            return ResponseEntity.ok(ApiResponse.success(PageResponse.of(doiTuongService.findByCategory(category, pageable))));
        }
        if (status != null) {
            return ResponseEntity.ok(ApiResponse.success(PageResponse.of(doiTuongService.findByStatus(status, pageable))));
        }
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(doiTuongService.findAll(pageable))));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Tạo đối tượng mới")
    public ResponseEntity<ApiResponse<DoiTuongHuongTruCap>> create(@RequestBody DoiTuongHuongTruCap doiTuong) {
        return ResponseEntity.ok(ApiResponse.success("Tạo đối tượng thành công", doiTuongService.create(doiTuong)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Cập nhật đối tượng")
    public ResponseEntity<ApiResponse<DoiTuongHuongTruCap>> update(@PathVariable String id, @RequestBody DoiTuongHuongTruCap updated) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", doiTuongService.update(id, updated)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa đối tượng")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        doiTuongService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa đối tượng thành công", null));
    }
}
