package com.trocap.auth.service;

import com.trocap.auth.dto.ChangePasswordRequest;
import com.trocap.auth.dto.LoginRequest;
import com.trocap.auth.dto.LoginResponse;
import com.trocap.auth.dto.RefreshTokenRequest;
import com.trocap.auth.dto.RegisterRequest;
import com.trocap.auth.dto.UpdateProfileRequest;
import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.model.RefreshToken;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.auth.repository.RefreshTokenRepository;
import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.common.exception.UnauthorizedException;
import com.trocap.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration; // 7 days default

    @Override
    public LoginResponse login(LoginRequest request) {
        // Dùng chung message để tránh leak thông tin user tồn tại
        String errorMsg = "Sai tên đăng nhập hoặc mật khẩu";

        NguoiDung user = nguoiDungRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException(errorMsg));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(errorMsg);
        }

        String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRoles());

        // Create refresh token
        refreshTokenRepository.deleteByNguoiDungId(user.getId());
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .nguoiDungId(user.getId())
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();
        refreshTokenRepository.save(refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public NguoiDung register(RegisterRequest request) {
        if (nguoiDungRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Tên đăng nhập đã tồn tại");
        }
        if (nguoiDungRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại");
        }

        // Register công khai luôn gán CITIZEN — không cho client tự chọn role
        NguoiDung user = NguoiDung.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .roles(List.of("CITIZEN"))
                .status("ACTIVE")
                .build();

        return nguoiDungRepository.save(user);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token không hợp lệ"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token đã hết hạn, vui lòng đăng nhập lại");
        }

        NguoiDung user = nguoiDungRepository.findById(refreshToken.getNguoiDungId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        String newAccessToken = jwtUtil.generateToken(user.getUsername(), user.getRoles());

        // Rotate refresh token
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        refreshTokenRepository.save(refreshToken);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public void logout(String username) {
        NguoiDung user = nguoiDungRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
        refreshTokenRepository.deleteByNguoiDungId(user.getId());
    }

    @Override
    public NguoiDung updateProfile(String username, UpdateProfileRequest request) {
        NguoiDung user = nguoiDungRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) {
            // Kiểm tra email trùng (trừ chính mình)
            nguoiDungRepository.findByEmail(request.getEmail())
                    .filter(u -> !u.getId().equals(user.getId()))
                    .ifPresent(u -> { throw new BadRequestException("Email đã tồn tại"); });
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCccd() != null) user.setCccd(request.getCccd());

        return nguoiDungRepository.save(user);
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        NguoiDung user = nguoiDungRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu hiện tại không đúng");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        nguoiDungRepository.save(user);
    }
}
