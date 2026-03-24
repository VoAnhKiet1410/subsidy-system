package com.trocap.auth.service;

import com.trocap.auth.dto.ChangePasswordRequest;
import com.trocap.auth.dto.LoginRequest;
import com.trocap.auth.dto.LoginResponse;
import com.trocap.auth.dto.RefreshTokenRequest;
import com.trocap.auth.dto.RegisterRequest;
import com.trocap.auth.dto.UpdateProfileRequest;
import com.trocap.auth.model.NguoiDung;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    NguoiDung register(RegisterRequest request);
    LoginResponse refreshToken(RefreshTokenRequest request);
    void logout(String username);
    NguoiDung updateProfile(String username, UpdateProfileRequest request);
    void changePassword(String username, ChangePasswordRequest request);
}
