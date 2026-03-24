package com.trocap.nguoidung.controller;

import com.trocap.common.dto.ApiResponse;
import com.trocap.nguoidung.dto.VaiTroRequest;
import com.trocap.nguoidung.model.VaiTro;
import com.trocap.nguoidung.service.VaiTroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Roles", description = "Quản lý vai trò — chỉ ADMIN")
public class VaiTroController {

    private final VaiTroService vaiTroService;

    @GetMapping
    @Operation(summary = "Lấy danh sách vai trò")
    public ResponseEntity<ApiResponse<List<VaiTro>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(vaiTroService.findAll()));
    }

    @PostMapping
    @Operation(summary = "Tạo vai trò mới")
    public ResponseEntity<ApiResponse<VaiTro>> create(@Valid @RequestBody VaiTroRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo vai trò thành công", vaiTroService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật vai trò")
    public ResponseEntity<ApiResponse<VaiTro>> update(@PathVariable String id,
                                                       @Valid @RequestBody VaiTroRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật vai trò thành công", vaiTroService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa vai trò")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        vaiTroService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa vai trò thành công", null));
    }
}
