package com.trocap.nguonquy.controller;

import com.trocap.common.dto.ApiResponse;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.nguonquy.model.NguonQuy;
import com.trocap.nguonquy.repository.NguonQuyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funding-sources")
@RequiredArgsConstructor
@Tag(name = "Funding Sources", description = "Nguồn quỹ hỗ trợ")
public class NguonQuyController {

    private final NguonQuyRepository nguonQuyRepository;

    @GetMapping
    @Operation(summary = "Lấy danh sách nguồn quỹ")
    public ResponseEntity<ApiResponse<List<NguonQuy>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(nguonQuyRepository.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết nguồn quỹ")
    public ResponseEntity<ApiResponse<NguonQuy>> findById(@PathVariable String id) {
        NguonQuy nq = nguonQuyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguồn quỹ: " + id));
        return ResponseEntity.ok(ApiResponse.success(nq));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Tạo nguồn quỹ mới")
    public ResponseEntity<ApiResponse<NguonQuy>> create(@RequestBody NguonQuy nguonQuy) {
        if (nguonQuy.getTrangThai() == null) nguonQuy.setTrangThai("ACTIVE");
        if (nguonQuy.getDaSuDung() == null) nguonQuy.setDaSuDung(0.0);
        nguonQuy.setConLai(nguonQuy.getTongNganSach() != null ? nguonQuy.getTongNganSach() - nguonQuy.getDaSuDung() : 0.0);
        return ResponseEntity.ok(ApiResponse.success("Tạo nguồn quỹ thành công", nguonQuyRepository.save(nguonQuy)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Cập nhật nguồn quỹ")
    public ResponseEntity<ApiResponse<NguonQuy>> update(@PathVariable String id, @RequestBody NguonQuy updated) {
        NguonQuy existing = nguonQuyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguồn quỹ: " + id));
        if (updated.getTenNguonQuy() != null) existing.setTenNguonQuy(updated.getTenNguonQuy());
        if (updated.getMoTa() != null) existing.setMoTa(updated.getMoTa());
        if (updated.getTongNganSach() != null) existing.setTongNganSach(updated.getTongNganSach());
        if (updated.getDaSuDung() != null) existing.setDaSuDung(updated.getDaSuDung());
        if (updated.getTrangThai() != null) existing.setTrangThai(updated.getTrangThai());
        existing.setConLai((existing.getTongNganSach() != null ? existing.getTongNganSach() : 0) - (existing.getDaSuDung() != null ? existing.getDaSuDung() : 0));
        return ResponseEntity.ok(ApiResponse.success("Cập nhật nguồn quỹ thành công", nguonQuyRepository.save(existing)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa nguồn quỹ")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        nguonQuyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguồn quỹ: " + id));
        nguonQuyRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa nguồn quỹ thành công", null));
    }
}
