package com.trocap.nguoidung.controller;

import com.trocap.common.dto.ApiResponse;
import com.trocap.nguoidung.dto.*;
import com.trocap.nguoidung.service.NguoiDungService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Quản lý người dùng")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách người dùng")
    public ResponseEntity<ApiResponse<List<UserResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(nguoiDungService.findAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy thông tin người dùng theo ID")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(nguoiDungService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo người dùng mới (ADMIN)")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo người dùng thành công", nguoiDungService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật thông tin người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable String id,
                                                             @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", nguoiDungService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa người dùng")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        nguoiDungService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công", null));
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Gán vai trò cho người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(@PathVariable String id,
                                                                  @Valid @RequestBody AssignRolesRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Gán vai trò thành công", nguoiDungService.assignRoles(id, request)));
    }
}
