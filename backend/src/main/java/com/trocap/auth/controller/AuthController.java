package com.trocap.auth.controller;

import com.trocap.auth.dto.*;
import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.service.AuthService;
import com.trocap.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Đăng ký, đăng nhập, refresh token")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", authService.login(request)));
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản")
    public ResponseEntity<ApiResponse<NguoiDung>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công", authService.register(request)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Làm mới token thành công", authService.refreshToken(request)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất")
    public ResponseEntity<ApiResponse<Void>> logout(Principal principal) {
        authService.logout(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
    }

    @PutMapping("/profile")
    @Operation(summary = "Cập nhật thông tin cá nhân")
    public ResponseEntity<ApiResponse<NguoiDung>> updateProfile(
            @RequestBody UpdateProfileRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật hồ sơ thành công",
                authService.updateProfile(principal.getName(), request)));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request, Principal principal) {
        authService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }
}
