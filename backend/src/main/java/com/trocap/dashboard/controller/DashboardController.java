package com.trocap.dashboard.controller;

import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.chitra.repository.ChiTraRepository;
import com.trocap.chuongtrinh.repository.ChuongTrinhRepository;
import com.trocap.common.dto.ApiResponse;
import com.trocap.doituong.repository.DoiTuongRepository;
import com.trocap.hoso.repository.HoSoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Bảng điều khiển")
public class DashboardController {

    private final NguoiDungRepository nguoiDungRepository;
    private final DoiTuongRepository doiTuongRepository;
    private final HoSoRepository hoSoRepository;
    private final ChuongTrinhRepository chuongTrinhRepository;
    private final ChiTraRepository chiTraRepository;

    @GetMapping("/stats")
    @Operation(summary = "Lấy thống kê tổng quan")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", nguoiDungRepository.count());
        stats.put("totalBeneficiaries", doiTuongRepository.count());
        stats.put("totalApplications", hoSoRepository.count());
        stats.put("totalPrograms", chuongTrinhRepository.count());
        stats.put("totalTransactions", chiTraRepository.count());
        stats.put("pendingApplications", hoSoRepository.countByTrangThai("SUBMITTED"));
        stats.put("approvedApplications", hoSoRepository.countByTrangThai("APPROVED"));
        stats.put("rejectedApplications", hoSoRepository.countByTrangThai("REJECTED"));
        stats.put("activeBeneficiaries", doiTuongRepository.countByStatus("ACTIVE"));
        stats.put("pendingBeneficiaries", doiTuongRepository.countByStatus("PENDING"));
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
