package com.trocap.danhgiaai.controller;

import com.trocap.common.dto.ApiResponse;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.danhgiaai.model.DanhGiaAi;
import com.trocap.danhgiaai.repository.DanhGiaAiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-reviews")
@RequiredArgsConstructor
@Tag(name = "AI Reviews", description = "Đánh giá AI cho hồ sơ")
public class DanhGiaAiController {

    private final DanhGiaAiRepository danhGiaAiRepository;

    @GetMapping("/by-application/{hoSoId}")
    @Operation(summary = "Lấy đánh giá AI theo hồ sơ")
    public ResponseEntity<ApiResponse<List<DanhGiaAi>>> findByHoSo(@PathVariable String hoSoId) {
        return ResponseEntity.ok(ApiResponse.success(danhGiaAiRepository.findByHoSoId(hoSoId)));
    }

    @GetMapping("/latest/{hoSoId}")
    @Operation(summary = "Lấy đánh giá AI mới nhất cho hồ sơ")
    public ResponseEntity<ApiResponse<DanhGiaAi>> findLatest(@PathVariable String hoSoId) {
        DanhGiaAi dg = danhGiaAiRepository.findFirstByHoSoIdOrderByCreatedAtDesc(hoSoId)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa có đánh giá AI cho hồ sơ: " + hoSoId));
        return ResponseEntity.ok(ApiResponse.success(dg));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Tạo đánh giá AI mới")
    public ResponseEntity<ApiResponse<DanhGiaAi>> create(@RequestBody DanhGiaAi danhGia) {
        if (danhGia.getTrangThai() == null) danhGia.setTrangThai("COMPLETED");
        return ResponseEntity.ok(ApiResponse.success("Tạo đánh giá AI thành công", danhGiaAiRepository.save(danhGia)));
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả đánh giá AI")
    public ResponseEntity<ApiResponse<List<DanhGiaAi>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(danhGiaAiRepository.findAll()));
    }
}
