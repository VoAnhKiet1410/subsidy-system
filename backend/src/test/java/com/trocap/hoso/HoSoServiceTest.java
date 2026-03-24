package com.trocap.hoso;

import com.trocap.TestDataFactory;
import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import com.trocap.chuongtrinh.repository.ChuongTrinhRepository;
import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.doituong.model.DoiTuongHuongTruCap;
import com.trocap.doituong.repository.DoiTuongRepository;
import com.trocap.hoso.dto.HoSoRequest;
import com.trocap.hoso.dto.RejectRequest;
import com.trocap.hoso.model.HoSoHoTro;
import com.trocap.hoso.repository.HoSoRepository;
import com.trocap.hoso.service.HoSoService;
import com.trocap.thongbao.repository.ThongBaoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test nghiệp vụ Hồ sơ hỗ trợ.
 * Ref: docs/test-cases-ho-so-ho-tro.md (HS-01 → HS-40)
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HoSoServiceTest {

    @Autowired HoSoService hoSoService;
    @Autowired HoSoRepository hoSoRepository;
    @Autowired NguoiDungRepository userRepo;
    @Autowired DoiTuongRepository doiTuongRepo;
    @Autowired ChuongTrinhRepository chuongTrinhRepo;
    @Autowired ThongBaoRepository thongBaoRepo;

    NguoiDung citizen, citizen2, officer;
    DoiTuongHuongTruCap doiTuong;
    ChuongTrinhTruCap chuongTrinh;

    @BeforeEach
    void setup() {
        hoSoRepository.deleteAll();
        userRepo.deleteAll();
        doiTuongRepo.deleteAll();
        chuongTrinhRepo.deleteAll();
        thongBaoRepo.deleteAll();

        citizen = userRepo.save(TestDataFactory.citizen());
        citizen2 = userRepo.save(TestDataFactory.citizen2());
        officer = userRepo.save(TestDataFactory.officer());
        doiTuong = doiTuongRepo.save(TestDataFactory.doiTuong("dt-01", "Hộ nghèo"));
        chuongTrinh = chuongTrinhRepo.save(TestDataFactory.chuongTrinh("ct-01", "Hỗ trợ 2024", "nq-01"));
    }

    // ═══════════════════════════════════════════════════════════
    //  A. TẠO HỒ SƠ
    // ═══════════════════════════════════════════════════════════

    /** HS-01: Tạo hồ sơ → mặc định DRAFT */
    @Test
    void create_defaultDraft() {
        HoSoHoTro hs = hoSoService.create(buildRequest(), citizen.getUsername());
        assertEquals("DRAFT", hs.getTrangThai());
        assertEquals(citizen.getId(), hs.getNguoiDungId());
        assertNotNull(hs.getId());
    }

    /** HS-02: Đối tượng không tồn tại → 400 */
    @Test
    void create_invalidDoiTuong() {
        HoSoRequest req = buildRequest();
        req.setDoiTuongId("INVALID");
        assertThrows(Exception.class,
                () -> hoSoService.create(req, citizen.getUsername()));
    }

    /** HS-03: Chương trình không tồn tại → 400 */
    @Test
    void create_invalidChuongTrinh() {
        HoSoRequest req = buildRequest();
        req.setChuongTrinhId("INVALID");
        assertThrows(Exception.class,
                () -> hoSoService.create(req, citizen.getUsername()));
    }

    // ═══════════════════════════════════════════════════════════
    //  B. SUBMIT
    // ═══════════════════════════════════════════════════════════

    /** HS-06: Submit hồ sơ DRAFT → SUBMITTED */
    @Test
    void submit_fromDraft() {
        HoSoHoTro hs = hoSoService.create(buildRequest(), citizen.getUsername());
        HoSoHoTro submitted = hoSoService.submit(hs.getId(), citizen.getUsername());
        assertEquals("SUBMITTED", submitted.getTrangThai());
        assertNotNull(submitted.getNgayNopHoSo());
    }

    /** HS-09: Submit hồ sơ SUBMITTED → lỗi */
    @Test
    void submit_fromSubmitted_fails() {
        HoSoHoTro hs = createAndSubmit();
        assertThrows(BadRequestException.class,
                () -> hoSoService.submit(hs.getId(), citizen.getUsername()));
    }

    /** HS-10: Submit hồ sơ APPROVED → lỗi */
    @Test
    void submit_fromApproved_fails() {
        HoSoHoTro hs = hoSoRepository.save(
                TestDataFactory.hoSo("hs-approved", citizen.getId(), doiTuong.getId(),
                        chuongTrinh.getId(), "APPROVED"));
        assertThrows(BadRequestException.class,
                () -> hoSoService.submit(hs.getId(), citizen.getUsername()));
    }

    /** HS-11: CITIZEN B submit hồ sơ của CITIZEN A → lỗi ownership */
    @Test
    void submit_otherUserHoSo_fails() {
        HoSoHoTro hs = hoSoService.create(buildRequest(), citizen.getUsername());
        assertThrows(BadRequestException.class,
                () -> hoSoService.submit(hs.getId(), citizen2.getUsername()));
    }

    // ═══════════════════════════════════════════════════════════
    //  C. WORKFLOW: UNDER_REVIEW → APPROVE → REJECT
    // ═══════════════════════════════════════════════════════════

    /** WF-01: SUBMITTED → UNDER_REVIEW */
    @Test
    void startReview_fromSubmitted() {
        HoSoHoTro hs = createAndSubmit();
        HoSoHoTro reviewed = hoSoService.startReview(hs.getId(), officer.getUsername());
        assertEquals("UNDER_REVIEW", reviewed.getTrangThai());
    }

    /** WF-03: DRAFT → UNDER_REVIEW → lỗi */
    @Test
    void startReview_fromDraft_fails() {
        HoSoHoTro hs = hoSoService.create(buildRequest(), citizen.getUsername());
        assertThrows(BadRequestException.class,
                () -> hoSoService.startReview(hs.getId(), officer.getUsername()));
    }

    /** WF-04: APPROVED → UNDER_REVIEW → lỗi */
    @Test
    void startReview_fromApproved_fails() {
        HoSoHoTro hs = hoSoRepository.save(
                TestDataFactory.hoSo("hs-app", citizen.getId(), doiTuong.getId(),
                        chuongTrinh.getId(), "APPROVED"));
        assertThrows(BadRequestException.class,
                () -> hoSoService.startReview(hs.getId(), officer.getUsername()));
    }

    /** WF-08: UNDER_REVIEW → APPROVED */
    @Test
    void approve_fromUnderReview() {
        HoSoHoTro hs = createSubmitAndReview();
        HoSoHoTro approved = hoSoService.approve(hs.getId(), officer.getUsername());
        assertEquals("APPROVED", approved.getTrangThai());
        assertNotNull(approved.getApprovedBy());
        assertNotNull(approved.getApprovedAt());
    }

    /** WF-09: Approve xóa lyDoTuChoi cũ */
    @Test
    void approve_clearsRejectionReason() {
        HoSoHoTro hs = hoSoRepository.save(HoSoHoTro.builder()
                .nguoiDungId(citizen.getId()).doiTuongId(doiTuong.getId())
                .chuongTrinhId(chuongTrinh.getId()).trangThai("UNDER_REVIEW")
                .lyDoTuChoi("Lý do cũ").build());
        HoSoHoTro approved = hoSoService.approve(hs.getId(), officer.getUsername());
        assertNull(approved.getLyDoTuChoi());
    }

    /** WF-10: SUBMITTED → APPROVED (skip under_review) → lỗi */
    @Test
    void approve_fromSubmitted_fails() {
        HoSoHoTro hs = createAndSubmit();
        assertThrows(BadRequestException.class,
                () -> hoSoService.approve(hs.getId(), officer.getUsername()));
    }

    /** WF-14: UNDER_REVIEW → REJECTED (có lý do) */
    @Test
    void reject_withReason() {
        HoSoHoTro hs = createSubmitAndReview();
        RejectRequest req = new RejectRequest();
        req.setLyDoTuChoi("Thiếu CMND");
        HoSoHoTro rejected = hoSoService.reject(hs.getId(), req, officer.getUsername());
        assertEquals("REJECTED", rejected.getTrangThai());
        assertEquals("Thiếu CMND", rejected.getLyDoTuChoi());
    }

    /** WF-15: Reject thiếu lý do → lỗi (service layer validation) */
    @Test
    void reject_withoutReason_fails() {
        HoSoHoTro hs = createSubmitAndReview();
        RejectRequest req = new RejectRequest();
        req.setLyDoTuChoi("");
        assertThrows(BadRequestException.class,
                () -> hoSoService.reject(hs.getId(), req, officer.getUsername()));
    }

    /** WF-16: Reject với lý do null → lỗi */
    @Test
    void reject_nullReason_fails() {
        HoSoHoTro hs = createSubmitAndReview();
        RejectRequest req = new RejectRequest(); // lyDoTuChoi defaults to null
        assertThrows(BadRequestException.class,
                () -> hoSoService.reject(hs.getId(), req, officer.getUsername()));
    }

    /** WF-34: REJECTED → UNDER_REVIEW (re-review) */
    @Test
    void startReview_fromRejected() {
        HoSoHoTro hs = hoSoRepository.save(
                TestDataFactory.hoSo("hs-rej", citizen.getId(), doiTuong.getId(),
                        chuongTrinh.getId(), "REJECTED"));
        HoSoHoTro reviewed = hoSoService.startReview(hs.getId(), officer.getUsername());
        assertEquals("UNDER_REVIEW", reviewed.getTrangThai());
    }

    /** WF-33: Approve tạo thông báo cho CITIZEN */
    @Test
    void approve_createsNotification() {
        HoSoHoTro hs = createSubmitAndReview();
        hoSoService.approve(hs.getId(), officer.getUsername());
        long count = thongBaoRepo.countByNguoiDungIdAndDaDoc(citizen.getId(), false);
        assertTrue(count >= 1, "Phải có ít nhất 1 thông báo cho citizen");
    }

    // ═══════════════════════════════════════════════════════════
    //  D. HELPERS
    // ═══════════════════════════════════════════════════════════

    private HoSoRequest buildRequest() {
        HoSoRequest req = new HoSoRequest();
        req.setDoiTuongId(doiTuong.getId());
        req.setChuongTrinhId(chuongTrinh.getId());
        req.setMoTa("Test hồ sơ");
        req.setSoTienDeXuat(5000000.0);
        return req;
    }

    private HoSoHoTro createAndSubmit() {
        HoSoHoTro hs = hoSoService.create(buildRequest(), citizen.getUsername());
        return hoSoService.submit(hs.getId(), citizen.getUsername());
    }

    private HoSoHoTro createSubmitAndReview() {
        HoSoHoTro hs = createAndSubmit();
        return hoSoService.startReview(hs.getId(), officer.getUsername());
    }
}
