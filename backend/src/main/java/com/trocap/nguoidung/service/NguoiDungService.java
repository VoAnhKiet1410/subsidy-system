package com.trocap.nguoidung.service;

import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.nguoidung.dto.*;
import com.trocap.nguoidung.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── Mapping helper ─────────────────────────────────────────────
    public UserResponse toResponse(NguoiDung user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .cccd(user.getCccd())
                .roles(user.getRoles())
                .status(user.getStatus())
                .avatar(user.getAvatar())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // ─── CRUD ────────────────────────────────────────────────────────
    public List<UserResponse> findAll() {
        return nguoiDungRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findById(String id) {
        NguoiDung user = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + id));
        return toResponse(user);
    }

    public UserResponse create(CreateUserRequest request) {
        if (nguoiDungRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Tên đăng nhập đã tồn tại");
        }
        if (nguoiDungRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại");
        }

        NguoiDung user = NguoiDung.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .cccd(request.getCccd())
                .roles(List.of("CITIZEN")) // Mặc định gán CITIZEN
                .status("ACTIVE")
                .build();

        return toResponse(nguoiDungRepository.save(user));
    }

    public UserResponse update(String id, UpdateUserRequest request) {
        NguoiDung user = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + id));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) {
            // Kiểm tra trùng email với user khác
            nguoiDungRepository.findByEmail(request.getEmail())
                    .filter(u -> !u.getId().equals(id))
                    .ifPresent(u -> { throw new BadRequestException("Email đã được sử dụng"); });
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCccd() != null) user.setCccd(request.getCccd());
        if (request.getStatus() != null) user.setStatus(request.getStatus());

        return toResponse(nguoiDungRepository.save(user));
    }

    public void delete(String id) {
        if (!nguoiDungRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng: " + id);
        }
        nguoiDungRepository.deleteById(id);
    }

    // ─── Assign roles ────────────────────────────────────────────────
    public UserResponse assignRoles(String userId, AssignRolesRequest request) {
        NguoiDung user = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + userId));

        // Validate tất cả role codes phải tồn tại trong collection roles
        List<String> validCodes = new ArrayList<>();
        for (String code : request.getRoleCodes()) {
            if (!vaiTroRepository.existsByMaVaiTro(code)) {
                throw new BadRequestException("Mã vai trò không tồn tại: " + code);
            }
            if (!validCodes.contains(code)) {
                validCodes.add(code);
            }
        }

        user.setRoles(validCodes);
        return toResponse(nguoiDungRepository.save(user));
    }
}
