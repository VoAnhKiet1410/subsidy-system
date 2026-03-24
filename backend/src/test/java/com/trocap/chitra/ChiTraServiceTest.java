package com.trocap.chitra;

import com.trocap.TestDataFactory;
import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.chitra.dto.PaymentRequest;
import com.trocap.chitra.dto.PaymentStatusRequest;
import com.trocap.chitra.model.ChiTraTruCap;
import com.trocap.chitra.repository.ChiTraRepository;
import com.trocap.chitra.service.ChiTraService;
import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import com.trocap.chuongtrinh.repository.ChuongTrinhRepository;
import com.trocap.common.exception.BadRequestException;
import com.trocap.doituong.model.DoiTuongHuongTruCap;
import com.trocap.doituong.repository.DoiTuongRepository;
import com.trocap.hoso.model.HoSoHoTro;
import com.trocap.hoso.repository.HoSoRepository;
import com.trocap.nguonquy.model.NguonQuy;
import com.trocap.nguonquy.repository.NguonQuyRepository;
import com.trocap.thongbao.repository.ThongBaoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test nghiệp vụ Chi trả trợ cấp.
 * Ref: docs/test-cases-duyet-chitrac.md (WF-20 → WF-35)
 */
@SpringBootTest
@ActiveProfiles("test")
class ChiTraServiceTest {

    @Autowired ChiTraService chiTraService;
    @Autowired ChiTraRepository chiTraRepo;
    @Autowired HoSoRepository hoSoRepo;
    @Autowired NguoiDungRepository userRepo;
    @Autowired DoiTuongRepository doiTuongRepo;
    @Autowired ChuongTrinhRepository chuongTrinhRepo;
    @Autowired NguonQuyRepository nguonQuyRepo;
    @Autowired ThongBaoRepository thongBaoRepo;

    NguoiDung citizen, accountant;
    NguonQuy nguonQuy;
    ChuongTrinhTruCap chuongTrinh;
    HoSoHoTro hoSoApproved;

    @BeforeEach
    void setup() {
        chiTraRepo.deleteAll();
        hoSoRepo.deleteAll();
        userRepo.deleteAll();
        doiTuongRepo.deleteAll();
        chuongTrinhRepo.deleteAll();
        nguonQuyRepo.deleteAll();
        thongBaoRepo.deleteAll();

        citizen = userRepo.save(TestDataFactory.citizen());
        accountant = userRepo.save(TestDataFactory.accountant());

        DoiTuongHuongTruCap dt = doiTuongRepo.save(TestDataFactory.doiTuong("dt-01", "Hộ nghèo"));
        nguonQuy = nguonQuyRepo.save(TestDataFactory.nguonQuy("nq-01", "Quỹ XHCN", 500000000, 100000000));
        chuongTrinh = chuongTrinhRepo.save(TestDataFactory.chuongTrinh("ct-01", "Hỗ trợ 2024", "nq-01"));

        hoSoApproved = hoSoRepo.save(TestDataFactory.hoSo(
                "hs-approved", citizen.getId(), dt.getId(), chuongTrinh.getId(), "APPROVED"));
    }

    // ═══════════════════════════════════════════════════════════
    //  A. TẠO PAYMENT
    // ═══════════════════════════════════════════════════════════

    /** WF-20: Tạo payment cho hồ sơ APPROVED → PENDING */
    @Test
    void create_forApprovedHoSo() {
        ChiTraTruCap payment = chiTraService.create(paymentRequest(5000000), accountant.getUsername());
        assertEquals("PENDING", payment.getTrangThai());
        assertEquals(hoSoApproved.getId(), payment.getHoSoHoTroId());
    }

    /** WF-21: Tạo payment cho hồ sơ DRAFT → lỗi */
    @Test
    void create_forDraftHoSo_fails() {
        HoSoHoTro draftHs = hoSoRepo.save(TestDataFactory.hoSo(
                "hs-draft", citizen.getId(), "dt-01", "ct-01", "DRAFT"));
        PaymentRequest req = new PaymentRequest();
        req.setHoSoHoTroId(draftHs.getId());
        req.setSoTien(5000000.0);
        req.setPhuongThuc("CASH");

        assertThrows(BadRequestException.class,
                () -> chiTraService.create(req, accountant.getUsername()));
    }

    /** WF-22: Tạo payment cho hồ sơ SUBMITTED → lỗi */
    @Test
    void create_forSubmittedHoSo_fails() {
        HoSoHoTro submittedHs = hoSoRepo.save(TestDataFactory.hoSo(
                "hs-sub", citizen.getId(), "dt-01", "ct-01", "SUBMITTED"));
        PaymentRequest req = new PaymentRequest();
        req.setHoSoHoTroId(submittedHs.getId());
        req.setSoTien(5000000.0);
        req.setPhuongThuc("CASH");

        assertThrows(BadRequestException.class,
                () -> chiTraService.create(req, accountant.getUsername()));
    }

    // ═══════════════════════════════════════════════════════════
    //  B. PAYMENT SUCCESS → SIDE EFFECTS
    // ═══════════════════════════════════════════════════════════

    /** WF-25: Payment SUCCESS → hồ sơ PAID + trừ ngân sách + thông báo */
    @Test
    void success_updatesHoSoAndBudgetAndNotification() {
        ChiTraTruCap payment = chiTraService.create(paymentRequest(5000000), accountant.getUsername());

        PaymentStatusRequest statusReq = new PaymentStatusRequest();
        statusReq.setTrangThai("SUCCESS");
        chiTraService.updateStatus(payment.getId(), statusReq, accountant.getUsername());

        // Hồ sơ chuyển sang PAID
        HoSoHoTro updated = hoSoRepo.findById(hoSoApproved.getId()).orElseThrow();
        assertEquals("PAID", updated.getTrangThai());

        // Ngân sách bị trừ
        NguonQuy updatedQuy = nguonQuyRepo.findById(nguonQuy.getId()).orElseThrow();
        assertEquals(395000000.0, updatedQuy.getConLai(), 0.01);

        // Có thông báo cho citizen
        long notiCount = thongBaoRepo.countByNguoiDungIdAndDaDoc(citizen.getId(), false);
        assertTrue(notiCount >= 1);
    }

    /** WF-26: Vượt ngân sách → lỗi */
    @Test
    void success_exceedsBudget_fails() {
        // Nguồn quỹ còn 400tr, chi 500tr
        ChiTraTruCap payment = chiTraService.create(paymentRequest(500000000), accountant.getUsername());

        PaymentStatusRequest statusReq = new PaymentStatusRequest();
        statusReq.setTrangThai("SUCCESS");

        assertThrows(BadRequestException.class,
                () -> chiTraService.updateStatus(payment.getId(), statusReq, accountant.getUsername()));
    }

    /** WF-27: Boundary — chi vừa đúng ngân sách còn lại */
    @Test
    void success_exactBudget_ok() {
        // Còn 400tr, chi đúng 400tr
        ChiTraTruCap payment = chiTraService.create(paymentRequest(400000000), accountant.getUsername());

        PaymentStatusRequest statusReq = new PaymentStatusRequest();
        statusReq.setTrangThai("SUCCESS");
        chiTraService.updateStatus(payment.getId(), statusReq, accountant.getUsername());

        NguonQuy updatedQuy = nguonQuyRepo.findById(nguonQuy.getId()).orElseThrow();
        assertEquals(0.0, updatedQuy.getConLai(), 0.01);
    }

    // ═══════════════════════════════════════════════════════════
    //  C. IMMUTABILITY — CHẶN THAY ĐỔI SAU KHI HOÀN TẤT
    // ═══════════════════════════════════════════════════════════

    /** WF-30: Không đổi sau SUCCESS */
    @Test
    void updateStatus_afterSuccess_fails() {
        ChiTraTruCap payment = chiTraRepo.save(TestDataFactory.payment(
                "pay-success", hoSoApproved.getId(), 5000000, "SUCCESS"));

        PaymentStatusRequest statusReq = new PaymentStatusRequest();
        statusReq.setTrangThai("CANCELLED");

        assertThrows(BadRequestException.class,
                () -> chiTraService.updateStatus(payment.getId(), statusReq, accountant.getUsername()));
    }

    /** WF-31: Không đổi sau CANCELLED */
    @Test
    void updateStatus_afterCancelled_fails() {
        ChiTraTruCap payment = chiTraRepo.save(TestDataFactory.payment(
                "pay-cancel", hoSoApproved.getId(), 5000000, "CANCELLED"));

        PaymentStatusRequest statusReq = new PaymentStatusRequest();
        statusReq.setTrangThai("SUCCESS");

        assertThrows(BadRequestException.class,
                () -> chiTraService.updateStatus(payment.getId(), statusReq, accountant.getUsername()));
    }

    /** WF-28: Không đổi sau FAILED */
    @Test
    void updateStatus_afterFailed_fails() {
        ChiTraTruCap payment = chiTraRepo.save(TestDataFactory.payment(
                "pay-fail", hoSoApproved.getId(), 5000000, "FAILED"));

        PaymentStatusRequest statusReq = new PaymentStatusRequest();
        statusReq.setTrangThai("SUCCESS");

        assertThrows(BadRequestException.class,
                () -> chiTraService.updateStatus(payment.getId(), statusReq, accountant.getUsername()));
    }

    /** Trạng thái không hợp lệ "INVALID" → lỗi */
    @Test
    void updateStatus_invalidStatus_fails() {
        ChiTraTruCap payment = chiTraService.create(paymentRequest(5000000), accountant.getUsername());

        PaymentStatusRequest statusReq = new PaymentStatusRequest();
        statusReq.setTrangThai("INVALID");

        assertThrows(BadRequestException.class,
                () -> chiTraService.updateStatus(payment.getId(), statusReq, accountant.getUsername()));
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════

    private PaymentRequest paymentRequest(double soTien) {
        PaymentRequest req = new PaymentRequest();
        req.setHoSoHoTroId(hoSoApproved.getId());
        req.setSoTien(soTien);
        req.setPhuongThuc("BANK_TRANSFER");
        return req;
    }
}
