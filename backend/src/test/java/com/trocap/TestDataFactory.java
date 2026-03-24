package com.trocap;

import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.model.RefreshToken;
import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import com.trocap.doituong.model.DoiTuongHuongTruCap;
import com.trocap.hoso.model.HoSoHoTro;
import com.trocap.nguonquy.model.NguonQuy;
import com.trocap.chitra.model.ChiTraTruCap;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Factory tạo dữ liệu test mẫu cho toàn bộ hệ thống.
 * Tham chiếu: docs/test-data.md
 */
public class TestDataFactory {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
    public static final String RAW_PASSWORD = "Abc@1234";

    // ─── Users ──────────────────────────────────────────────────
    public static NguoiDung admin() {
        return NguoiDung.builder()
                .id("u-admin").username("admin").password(ENCODER.encode(RAW_PASSWORD))
                .email("admin@trocap.vn").fullName("Quản trị viên")
                .roles(List.of("ADMIN")).status("ACTIVE").build();
    }

    public static NguoiDung officer() {
        return NguoiDung.builder()
                .id("u-officer").username("officer01").password(ENCODER.encode(RAW_PASSWORD))
                .email("officer01@trocap.vn").fullName("Trần Văn B")
                .roles(List.of("OFFICER")).status("ACTIVE").build();
    }

    public static NguoiDung accountant() {
        return NguoiDung.builder()
                .id("u-accountant").username("accountant01").password(ENCODER.encode(RAW_PASSWORD))
                .email("acc01@trocap.vn").fullName("Lê Thị C")
                .roles(List.of("ACCOUNTANT")).status("ACTIVE").build();
    }

    public static NguoiDung citizen() {
        return NguoiDung.builder()
                .id("u-citizen1").username("citizen01").password(ENCODER.encode(RAW_PASSWORD))
                .email("citizen01@mail.com").fullName("Nguyễn Văn A")
                .roles(List.of("CITIZEN")).status("ACTIVE").build();
    }

    public static NguoiDung citizen2() {
        return NguoiDung.builder()
                .id("u-citizen2").username("citizen02").password(ENCODER.encode(RAW_PASSWORD))
                .email("citizen02@mail.com").fullName("Phạm Thị D")
                .roles(List.of("CITIZEN")).status("ACTIVE").build();
    }

    // ─── Đối tượng ──────────────────────────────────────────────
    public static DoiTuongHuongTruCap doiTuong(String id, String fullName) {
        return DoiTuongHuongTruCap.builder()
                .id(id).fullName(fullName).code("DT-" + id)
                .category("HO_NGHEO").status("ACTIVE").build();
    }

    // ─── Nguồn quỹ ──────────────────────────────────────────────
    public static NguonQuy nguonQuy(String id, String ten, double tongNganSach, double daSuDung) {
        return NguonQuy.builder()
                .id(id).tenNguonQuy(ten)
                .tongNganSach(tongNganSach).daSuDung(daSuDung)
                .conLai(tongNganSach - daSuDung)
                .loai("NGAN_SACH_NHA_NUOC").trangThai("ACTIVE").build();
    }

    // ─── Chương trình ───────────────────────────────────────────
    public static ChuongTrinhTruCap chuongTrinh(String id, String ten, String nguonQuyId) {
        return ChuongTrinhTruCap.builder()
                .id(id).tenChuongTrinh(ten).nguonQuyId(nguonQuyId)
                .ngayBatDau(LocalDate.of(2024, 1, 1))
                .ngayKetThuc(LocalDate.of(2024, 12, 31))
                .trangThai("OPEN").daChi(0.0).build();
    }

    // ─── Hồ sơ ──────────────────────────────────────────────────
    public static HoSoHoTro hoSo(String id, String userId, String doiTuongId,
                                  String chuongTrinhId, String trangThai) {
        return HoSoHoTro.builder()
                .id(id).nguoiDungId(userId)
                .doiTuongId(doiTuongId).chuongTrinhId(chuongTrinhId)
                .trangThai(trangThai).moTa("Test hồ sơ")
                .soTienDeXuat(5000000.0).build();
    }

    // ─── Chi trả ────────────────────────────────────────────────
    public static ChiTraTruCap payment(String id, String hoSoId, double soTien, String trangThai) {
        return ChiTraTruCap.builder()
                .id(id).hoSoHoTroId(hoSoId).soTien(soTien)
                .phuongThuc("BANK_TRANSFER").trangThai(trangThai)
                .ngayChiTra(LocalDate.now()).processedBy("accountant01").build();
    }

    // ─── Refresh Token ──────────────────────────────────────────
    public static RefreshToken refreshToken(String userId) {
        return RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .nguoiDungId(userId)
                .expiryDate(Instant.now().plusSeconds(86400))
                .build();
    }
}
