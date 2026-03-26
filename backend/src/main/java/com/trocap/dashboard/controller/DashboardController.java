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

        // Phân bổ theo danh mục
        String[] categories = {"THU_NHAP_THAP", "KHUYET_TAT", "NGUOI_GIA", "TRE_EM", "DON_THAN", "HIV", "THIEN_TAI"};
        long totalBeneficiaries = doiTuongRepository.count();
        Map<String, Object> categoryDist = new HashMap<>();
        for (String cat : categories) {
            long count = doiTuongRepository.countByCategory(cat);
            if (count > 0) {
                categoryDist.put(cat, totalBeneficiaries > 0
                    ? Math.round(count * 100.0 / totalBeneficiaries) : 0);
            }
        }
        stats.put("categoryDistribution", categoryDist);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/beneficiary-counts")
    @Operation(summary = "Số đối tượng theo từng chương trình")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBeneficiaryCounts() {
        Map<String, Long> counts = new HashMap<>();
        chuongTrinhRepository.findAll().forEach(p ->
            counts.put(p.getId(), doiTuongRepository.countByProgramId(p.getId()))
        );
        return ResponseEntity.ok(ApiResponse.success(counts));
    }
}
