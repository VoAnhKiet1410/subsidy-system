package com.trocap.hoso.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trocap.hoso.model.HoSoHoTro;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO trả về cho frontend — chứa đầy đủ thông tin joined từ các collection liên quan.
 * Frontend (Vue) expect các field snake_case này.
 */
@Data
@Builder
public class HoSoResponse {

    private String id;

    /* ── Trạng thái & ngày ───────────────────────── */
    @JsonProperty("trang_thai")
    private String trangThai;

    @JsonProperty("trang_thai_chi_tra")
    private String trangThaiChiTra;

    @JsonProperty("ngay_nop_ho_so")
    private LocalDate ngayNopHoSo;

    @JsonProperty("mo_ta")
    private String moTa;

    @JsonProperty("so_tien_de_xuat")
    private Double soTienDeXuat;

    @JsonProperty("ly_do_tu_choi")
    private String lyDoTuChoi;

    @JsonProperty("reviewed_by")
    private String reviewedBy;

    @JsonProperty("approved_by")
    private String approvedBy;

    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    /* ── Nested: Người nộp (NguoiDung) ──────────── */
    @JsonProperty("nguoi_dung")
    private NguoiDungInfo nguoiDung;

    /* ── Nested: Đối tượng hưởng ─────────────────── */
    @JsonProperty("doi_tuong")
    private DoiTuongInfo doiTuong;

    /* ── Nested: Chương trình trợ cấp ───────────── */
    @JsonProperty("chuong_trinh")
    private ChuongTrinhInfo chuongTrinh;

    /* ═══════ Inner DTOs ═══════ */

    @Data
    @Builder
    public static class NguoiDungInfo {
        private String id;
        @JsonProperty("ten_day_du")
        private String tenDayDu;
        private String username;
        private String email;
        private String phone;
    }

    @Data
    @Builder
    public static class DoiTuongInfo {
        private String id;
        @JsonProperty("ten_doi_tuong")
        private String tenDoiTuong;
        private String category;
        private String status;
        @JsonProperty("ai_trust_score")
        private Integer aiTrustScore;
    }

    @Data
    @Builder
    public static class ChuongTrinhInfo {
        private String id;
        @JsonProperty("ten_chuong_trinh")
        private String tenChuongTrinh;
        @JsonProperty("trang_thai")
        private String trangThai;
        @JsonProperty("ngay_bat_dau")
        private LocalDate ngayBatDau;
        @JsonProperty("ngay_ket_thuc")
        private LocalDate ngayKetThuc;
    }

    /* ── Builder từ entity ─────────────────────── */
    public static HoSoResponse from(HoSoHoTro entity) {
        return HoSoResponse.builder()
                .id(entity.getId())
                .trangThai(entity.getTrangThai())
                .trangThaiChiTra(entity.getTrangThaiChiTra())
                .ngayNopHoSo(entity.getNgayNopHoSo())
                .moTa(entity.getMoTa())
                .soTienDeXuat(entity.getSoTienDeXuat())
                .lyDoTuChoi(entity.getLyDoTuChoi())
                .reviewedBy(entity.getReviewedBy())
                .approvedBy(entity.getApprovedBy())
                .approvedAt(entity.getApprovedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
