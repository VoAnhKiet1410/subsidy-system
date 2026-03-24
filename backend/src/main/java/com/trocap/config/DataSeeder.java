package com.trocap.config;

import com.trocap.auditlog.model.AuditLog;
import com.trocap.auditlog.repository.AuditLogRepository;
import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.chitra.model.ChiTraTruCap;
import com.trocap.chitra.repository.ChiTraRepository;
import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import com.trocap.chuongtrinh.repository.ChuongTrinhRepository;
import com.trocap.doituong.model.DoiTuongHuongTruCap;
import com.trocap.doituong.repository.DoiTuongRepository;
import com.trocap.hoso.model.HoSoHoTro;
import com.trocap.hoso.repository.HoSoRepository;
import com.trocap.thongbao.model.ThongBao;
import com.trocap.thongbao.repository.ThongBaoRepository;
import com.trocap.nguonquy.model.NguonQuy;
import com.trocap.nguonquy.repository.NguonQuyRepository;
import com.trocap.doituong.model.NhomDoiTuong;
import com.trocap.doituong.repository.NhomDoiTuongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ChuongTrinhRepository chuongTrinhRepository;
    private final DoiTuongRepository doiTuongRepository;
    private final ChiTraRepository chiTraRepository;
    private final HoSoRepository hoSoRepository;
    private final AuditLogRepository auditLogRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ThongBaoRepository thongBaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final NguonQuyRepository nguonQuyRepository;
    private final NhomDoiTuongRepository nhomDoiTuongRepository;

    @Override
    public void run(String... args) {
        // Luôn đảm bảo user mặc định có password chuẩn (chạy mỗi lần khởi động)
        seedUsers();

        // Luôn seed funds nếu chưa có
        List<NguonQuy> funds = seedFunds();
        List<NhomDoiTuong> groups = seedBeneficiaryGroups();

        if (chuongTrinhRepository.count() > 0) {
            log.info("Dữ liệu nghiệp vụ đã tồn tại, bỏ qua seed.");
            return;
        }
        log.info("Bắt đầu khởi tạo dữ liệu mẫu...");
        
        List<ChuongTrinhTruCap> programs = seedPrograms(funds);
        List<DoiTuongHuongTruCap> beneficiaries = seedBeneficiaries(programs);
        seedTransactions(programs, beneficiaries);
        seedApplications(beneficiaries);
        seedAuditLogs();
        seedNotifications();
        log.info("Khởi tạo dữ liệu mẫu hoàn tất!");
    }

    private void seedUsers() {
        // Danh sách user mặc định — luôn đảm bảo tồn tại với password chuẩn
        record DefaultUser(String username, String password, String email, String fullName, List<String> roles) {}

        List<DefaultUser> defaults = List.of(
            new DefaultUser("admin", "123456", "admin@trocap.gov.vn", "Quản trị viên", List.of("ADMIN")),
            new DefaultUser("officer1", "123456", "officer@trocap.gov.vn", "Trần Duy Minh", List.of("OFFICER")),
            new DefaultUser("accountant1", "123456", "accountant@trocap.gov.vn", "Mai Hoa Phương", List.of("ACCOUNTANT")),
            new DefaultUser("citizen1", "123456", "citizen@trocap.gov.vn", "Lê Thanh Hùng", List.of("CITIZEN"))
        );

        for (DefaultUser du : defaults) {
            nguoiDungRepository.findByUsername(du.username()).ifPresentOrElse(
                existing -> {
                    // User đã tồn tại → cập nhật password để đảm bảo đúng
                    existing.setPassword(passwordEncoder.encode(du.password()));
                    existing.setRoles(du.roles());
                    existing.setStatus("ACTIVE");
                    nguoiDungRepository.save(existing);
                    log.info("Đã cập nhật user: {} (password reset về mặc định)", du.username());
                },
                () -> {
                    // User chưa tồn tại → tạo mới
                    NguoiDung user = NguoiDung.builder()
                        .username(du.username())
                        .password(passwordEncoder.encode(du.password()))
                        .email(du.email())
                        .fullName(du.fullName())
                        .roles(du.roles())
                        .status("ACTIVE")
                        .build();
                    nguoiDungRepository.save(user);
                    log.info("Đã tạo user mới: {}", du.username());
                }
            );
        }
    }

    private List<NguonQuy> seedFunds() {
        if (nguonQuyRepository.count() > 0) {
            return nguonQuyRepository.findAll();
        }
        List<NguonQuy> funds = List.of(
            NguonQuy.builder().tenNguonQuy("Quỹ BTXH Tỉnh 2026").moTa("Ngân sách nhà nước phân bổ cho hoạt động bảo trợ năm 2026").tongNganSach(50000000000.0).daSuDung(12000000000.0).conLai(38000000000.0).loai("NGAN_SACH_NHA_NUOC").trangThai("ACTIVE").createdBy("admin").build(),
            NguonQuy.builder().tenNguonQuy("Quỹ Hỗ trợ Đồng bào Lũ lụt").moTa("Quỹ từ thiện đóng góp từ các cá nhân tổ chức").tongNganSach(10000000000.0).daSuDung(8000000000.0).conLai(2000000000.0).loai("XA_HOI_HOA").trangThai("ACTIVE").createdBy("admin").build(),
            NguonQuy.builder().tenNguonQuy("Quỹ Trẻ em Việt Nam").moTa("Tổ chức UNICEF tài trợ chi trả học phí").tongNganSach(20000000000.0).daSuDung(500000000.0).conLai(19500000000.0).loai("TAI_TRO").trangThai("ACTIVE").createdBy("admin").build()
        );
        return nguonQuyRepository.saveAll(funds);
    }

    private List<NhomDoiTuong> seedBeneficiaryGroups() {
        if (nhomDoiTuongRepository.count() > 0) {
            return nhomDoiTuongRepository.findAll();
        }
        List<NhomDoiTuong> groups = List.of(
            NhomDoiTuong.builder().tenDoiTuong("Người cao tuổi cô đơn").moTa("Người trên 60 tuổi không nơi nương tựa").build(),
            NhomDoiTuong.builder().tenDoiTuong("Người khuyết tật đặc biệt nặng").moTa("Người không tự phục vụ sinh hoạt cá nhân").build(),
            NhomDoiTuong.builder().tenDoiTuong("Trẻ em mồ côi cả cha lẫn mẹ").moTa("Dưới 18 tuổi").build(),
            NhomDoiTuong.builder().tenDoiTuong("Hộ nghèo / Cận nghèo").moTa("Sổ hộ nghèo có xác nhận").build()
        );
        return nhomDoiTuongRepository.saveAll(groups);
    }

    private List<ChuongTrinhTruCap> seedPrograms(List<NguonQuy> funds) {
        String quyId1 = funds.isEmpty() ? null : funds.get(0).getId();
        String quyId2 = funds.size() > 1 ? funds.get(1).getId() : null;
        String quyId3 = funds.size() > 2 ? funds.get(2).getId() : null;

        List<ChuongTrinhTruCap> programs = List.of(
            ChuongTrinhTruCap.builder().tenChuongTrinh("Hỗ trợ Nhà ở cho hộ nghèo").moTa("Chương trình trợ cấp tiền thuê nhà và sửa chữa nhà ở cho hộ nghèo, cận nghèo trên toàn quốc.")
                .nganSach(4_500_000_000.0).daChi(2_850_000_000.0).trangThai("OPEN").nguonQuyId(quyId1)
                .ngayBatDau(LocalDate.of(2024, 1, 15)).createdBy("admin").build(),
            ChuongTrinhTruCap.builder().tenChuongTrinh("Trợ cấp Giáo dục vùng cao").moTa("Hỗ trợ học phí, sách vở và dụng cụ học tập cho học sinh vùng cao, vùng đặc biệt khó khăn.")
                .nganSach(2_800_000_000.0).daChi(1_920_000_000.0).trangThai("OPEN").nguonQuyId(quyId3)
                .ngayBatDau(LocalDate.of(2024, 3, 1)).createdBy("admin").build(),
            ChuongTrinhTruCap.builder().tenChuongTrinh("Bảo hiểm Y tế hộ nghèo").moTa("Cấp thẻ bảo hiểm y tế miễn phí và hỗ trợ chi phí khám chữa bệnh cho hộ nghèo.")
                .nganSach(3_200_000_000.0).daChi(2_100_000_000.0).trangThai("OPEN").nguonQuyId(quyId1)
                .ngayBatDau(LocalDate.of(2024, 2, 1)).createdBy("admin").build(),
            ChuongTrinhTruCap.builder().tenChuongTrinh("Trợ cấp Người khuyết tật").moTa("Trợ cấp hàng tháng cho người khuyết tật nặng và đặc biệt nặng không có khả năng lao động.")
                .nganSach(1_500_000_000.0).daChi(980_000_000.0).trangThai("OPEN").nguonQuyId(quyId1)
                .ngayBatDau(LocalDate.of(2024, 1, 1)).createdBy("admin").build(),
            ChuongTrinhTruCap.builder().tenChuongTrinh("Hỗ trợ Thất nghiệp Q1-2024").moTa("Chương trình hỗ trợ người lao động mất việc do doanh nghiệp giải thể, phá sản.")
                .nganSach(800_000_000.0).daChi(750_000_000.0).trangThai("CLOSED").nguonQuyId(quyId2)
                .ngayBatDau(LocalDate.of(2024, 1, 1)).ngayKetThuc(LocalDate.of(2024, 3, 31)).createdBy("admin").build()
        );
        return chuongTrinhRepository.saveAll(programs);
    }

    private List<DoiTuongHuongTruCap> seedBeneficiaries(List<ChuongTrinhTruCap> programs) {
        String[][] names = {
            {"Nguyễn Văn Mạnh", "0912345001", "Hà Nội", "THU_NHAP_THAP"},
            {"Trần Thị Mai", "0912345002", "TP. Hồ Chí Minh", "THU_NHAP_THAP"},
            {"Lê Minh Tuấn", "0912345003", "Đà Nẵng", "TRE_EM"},
            {"Phạm Hồng Sơn", "0912345004", "Hải Phòng", "KHUYET_TAT"},
            {"Hoàng Thị Lan", "0912345005", "Cần Thơ", "NGUOI_GIA"},
            {"Võ Đức Thắng", "0912345006", "Nghệ An", "THU_NHAP_THAP"},
            {"Đặng Thị Hoa", "0912345007", "Thanh Hóa", "DON_THAN"},
            {"Bùi Quang Huy", "0912345008", "Bình Dương", "THU_NHAP_THAP"},
            {"Ngô Thanh Tùng", "0912345009", "Quảng Ninh", "KHUYET_TAT"},
            {"Lý Thị Phượng", "0912345010", "Lâm Đồng", "NGUOI_GIA"},
            {"Trương Văn Đạt", "0912345011", "Bắc Giang", "TRE_EM"},
            {"Đinh Thị Ngọc", "0912345012", "Quảng Nam", "THU_NHAP_THAP"},
            {"Phan Văn Lợi", "0912345013", "Bến Tre", "NGUOI_GIA"},
            {"Huỳnh Minh Châu", "0912345014", "Khánh Hòa", "DON_THAN"},
            {"Dương Thị Yến", "0912345015", "Sơn La", "TRE_EM"},
            {"Mai Xuân Long", "0912345016", "Lào Cai", "HIV"},
            {"Tạ Quốc Bảo", "0912345017", "Hà Giang", "THIEN_TAI"},
            {"Châu Thị Kim", "0912345018", "An Giang", "THU_NHAP_THAP"},
            {"Lương Văn Hải", "0912345019", "Gia Lai", "KHUYET_TAT"},
            {"Nguyễn Thị Tuyết", "0912345020", "Thái Nguyên", "NGUOI_GIA"},
        };

        String[] statuses = {"ACTIVE", "ACTIVE", "ACTIVE", "ACTIVE", "ACTIVE", "PENDING", "PENDING", "PENDING", "ACTIVE", "ACTIVE", "ACTIVE", "PAUSED", "ACTIVE", "PENDING", "ACTIVE", "REJECTED", "ACTIVE", "ACTIVE", "PENDING", "ACTIVE"};

        double[] amounts = {3_500_000, 2_800_000, 1_500_000, 4_200_000, 1_800_000, 3_200_000, 2_500_000, 3_000_000, 4_000_000, 1_600_000, 1_200_000, 2_900_000, 1_700_000, 2_600_000, 1_400_000, 0, 3_800_000, 2_700_000, 3_500_000, 1_900_000};

        List<DoiTuongHuongTruCap> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            int progIdx = i % programs.size();
            int trustScore = 60 + ThreadLocalRandom.current().nextInt(40);
            list.add(DoiTuongHuongTruCap.builder()
                .code("#HS-" + (10001 + i))
                .fullName(names[i][0])
                .phone(names[i][1])
                .address(names[i][2])
                .idNumber("0" + (79_000_000_000L + i))
                .category(names[i][3])
                .status(statuses[i])
                .programId(programs.get(progIdx).getId())
                .subsidyAmount(amounts[i])
                .startDate(LocalDate.of(2024, 1 + (i % 6), 1 + (i % 28)))
                .aiTrustScore(trustScore)
                .build()
            );
        }
        return doiTuongRepository.saveAll(list);
    }

    private void seedTransactions(List<ChuongTrinhTruCap> programs, List<DoiTuongHuongTruCap> beneficiaries) {
        List<ChiTraTruCap> txs = new ArrayList<>();
        String[] txStatuses = {"SUCCESS", "PENDING", "FAILED"};

        for (int i = 0; i < Math.min(5, beneficiaries.size()); i++) {
            DoiTuongHuongTruCap b = beneficiaries.get(i);
            txs.add(ChiTraTruCap.builder()
                .hoSoHoTroId("hoso-" + i)                       // placeholder ref
                .soTien(b.getSubsidyAmount() != null ? b.getSubsidyAmount() : 2_000_000.0)
                .phuongThuc(i % 2 == 0 ? "BANK_TRANSFER" : "CASH")
                .trangThai(txStatuses[i % 3])
                .ngayChiTra(java.time.LocalDate.now().minusDays(5 - i))
                .processedBy("admin")
                .build()
            );
        }
        chiTraRepository.saveAll(txs);
    }

    private void seedApplications(List<DoiTuongHuongTruCap> beneficiaries) {
        String[] statuses = {"DRAFT", "SUBMITTED", "UNDER_REVIEW", "APPROVED", "REJECTED"};

        List<HoSoHoTro> apps = new ArrayList<>();
        for (int i = 0; i < Math.min(5, beneficiaries.size()); i++) {
            DoiTuongHuongTruCap b = beneficiaries.get(i);

            apps.add(HoSoHoTro.builder()
                .nguoiDungId("citizen1")        // ref tới user
                .doiTuongId(b.getId())           // ref tới beneficiary
                .chuongTrinhId(b.getProgramId()) // ref tới program
                .moTa("Hồ sơ đề nghị hỗ trợ cho " + b.getFullName())
                .trangThai(statuses[i])
                .soTienDeXuat(b.getSubsidyAmount())
                .ngayNopHoSo(i > 0 ? java.time.LocalDate.now().minusDays(10 - i) : null)
                .build()
            );
        }
        hoSoRepository.saveAll(apps);
    }

    private void seedAuditLogs() {
        List<AuditLog> logs = List.of(
            AuditLog.builder().username("admin").action("LOGIN").entityType("User").details("Đăng nhập hệ thống").ipAddress("192.168.1.100").build(),
            AuditLog.builder().username("admin").action("CREATE").entityType("Program").details("Tạo chương trình Hỗ trợ Nhà ở").ipAddress("192.168.1.100").build(),
            AuditLog.builder().username("officer1").action("REVIEW").entityType("Application").details("Xem xét hồ sơ #HS-992811").ipAddress("192.168.1.101").build(),
            AuditLog.builder().username("admin").action("APPROVE").entityType("Application").details("Phê duyệt hồ sơ #HS-992814").ipAddress("192.168.1.100").build(),
            AuditLog.builder().username("accountant1").action("DISBURSE").entityType("Transaction").details("Giải ngân TRX-99201 cho Nguyễn Văn Mạnh").ipAddress("192.168.1.102").build()
        );
        auditLogRepository.saveAll(logs);
    }

    private void seedNotifications() {
        List<ThongBao> notifications = List.of(
            ThongBao.builder().nguoiDungId(null).loai("SUCCESS")
                .tieuDe("Phê duyệt thành công").noiDung("Hồ sơ #HS-992814 đã được phê duyệt bởi cấp huyện")
                .entityType("Application").link("/xet-duyet-ai").daDoc(false).build(),
            ThongBao.builder().nguoiDungId(null).loai("WARNING")
                .tieuDe("Hồ sơ cần xét duyệt").noiDung("Có 3 hồ sơ đang chờ xét duyệt cấp xã")
                .entityType("Application").link("/xet-duyet-ai").daDoc(false).build(),
            ThongBao.builder().nguoiDungId(null).loai("SUCCESS")
                .tieuDe("Giải ngân thành công").noiDung("Đã giải ngân 3,500,000đ cho Nguyễn Văn Mạnh (TRX-99201)")
                .entityType("Transaction").link("/ngan-sach").daDoc(false).build(),
            ThongBao.builder().nguoiDungId(null).loai("INFO")
                .tieuDe("Chương trình mới").noiDung("Chương trình \"Hỗ trợ Nhà ở\" vừa được tạo và mở đăng ký")
                .entityType("Program").link("/chuong-trinh").daDoc(true).build(),
            ThongBao.builder().nguoiDungId("admin").loai("WARNING")
                .tieuDe("Ngân sách sắp hết").noiDung("Chương trình Hỗ trợ Thất nghiệp đã dùng 94% ngân sách")
                .entityType("Program").link("/ngan-sach").daDoc(false).build(),
            ThongBao.builder().nguoiDungId("admin").loai("ERROR")
                .tieuDe("Lỗi xác minh tài liệu").noiDung("Hồ sơ #HS-992812 có tài liệu chưa được xác minh")
                .entityType("Application").link("/xet-duyet-ai").daDoc(false).build()
        );
        thongBaoRepository.saveAll(notifications);
    }
}
