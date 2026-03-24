package com.trocap.tailieu;

import com.trocap.TestDataFactory;
import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.chuongtrinh.repository.ChuongTrinhRepository;
import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.doituong.repository.DoiTuongRepository;
import com.trocap.hoso.model.HoSoHoTro;
import com.trocap.hoso.repository.HoSoRepository;
import com.trocap.tailieu.model.TaiLieuDinhKem;
import com.trocap.tailieu.repository.TaiLieuRepository;
import com.trocap.tailieu.service.TaiLieuService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test nghiệp vụ Tài liệu đính kèm + OCR.
 * Ref: docs/test-cases-upload-ocr.md (UPL-01 → UPL-19, OCR-01 → OCR-06)
 */
@SpringBootTest
@ActiveProfiles("test")
class TaiLieuServiceTest {

    @Autowired TaiLieuService taiLieuService;
    @Autowired TaiLieuRepository taiLieuRepo;
    @Autowired HoSoRepository hoSoRepo;
    @Autowired NguoiDungRepository userRepo;
    @Autowired DoiTuongRepository doiTuongRepo;
    @Autowired ChuongTrinhRepository chuongTrinhRepo;

    NguoiDung citizen, citizen2, officer;
    HoSoHoTro hoSo;

    @BeforeEach
    void setup() {
        taiLieuRepo.deleteAll();
        hoSoRepo.deleteAll();
        userRepo.deleteAll();
        doiTuongRepo.deleteAll();
        chuongTrinhRepo.deleteAll();

        citizen = userRepo.save(TestDataFactory.citizen());
        citizen2 = userRepo.save(TestDataFactory.citizen2());
        officer = userRepo.save(TestDataFactory.officer());

        doiTuongRepo.save(TestDataFactory.doiTuong("dt-01", "Hộ nghèo"));
        chuongTrinhRepo.save(TestDataFactory.chuongTrinh("ct-01", "Hỗ trợ 2024", "nq-01"));

        hoSo = hoSoRepo.save(TestDataFactory.hoSo(
                "hs-01", citizen.getId(), "dt-01", "ct-01", "DRAFT"));
    }

    // ═══════════════════════════════════════════════════════════
    //  A. UPLOAD THÀNH CÔNG
    // ═══════════════════════════════════════════════════════════

    /** UPL-01: Upload PDF thành công */
    @Test
    void upload_pdf_success() {
        MultipartFile file = pdfFile("test.pdf");
        TaiLieuDinhKem tl = taiLieuService.upload(hoSo.getId(), file, citizen.getUsername());
        assertNotNull(tl.getId());
        assertEquals(hoSo.getId(), tl.getHoSoHoTroId());
        assertNotNull(tl.getDuongDanFile());
    }

    /** UPL-02: Upload PNG */
    @Test
    void upload_png_success() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "scan.png", "image/png", "PNG content".getBytes());
        TaiLieuDinhKem tl = taiLieuService.upload(hoSo.getId(), file, citizen.getUsername());
        assertNotNull(tl.getId());
    }

    /** UPL-03: Upload JPG */
    @Test
    void upload_jpg_success() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "JPG content".getBytes());
        TaiLieuDinhKem tl = taiLieuService.upload(hoSo.getId(), file, citizen.getUsername());
        assertNotNull(tl.getId());
    }

    // ═══════════════════════════════════════════════════════════
    //  B. UPLOAD THẤT BẠI
    // ═══════════════════════════════════════════════════════════

    /** UPL-05: Upload .exe → bị chặn */
    @Test
    void upload_exe_rejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "virus.exe", "application/x-msdownload", "exe content".getBytes());
        assertThrows(BadRequestException.class,
                () -> taiLieuService.upload(hoSo.getId(), file, citizen.getUsername()));
    }

    /** UPL-06: Upload .docx → bị chặn */
    @Test
    void upload_docx_rejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "docx content".getBytes());
        assertThrows(BadRequestException.class,
                () -> taiLieuService.upload(hoSo.getId(), file, citizen.getUsername()));
    }

    /** UPL-08: Upload file rỗng → lỗi */
    @Test
    void upload_emptyFile_rejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]);
        assertThrows(BadRequestException.class,
                () -> taiLieuService.upload(hoSo.getId(), file, citizen.getUsername()));
    }

    /** UPL-10: Upload cho hồ sơ không tồn tại → lỗi */
    @Test
    void upload_invalidHoSo_fails() {
        MultipartFile file = pdfFile("test.pdf");
        assertThrows(ResourceNotFoundException.class,
                () -> taiLieuService.upload("INVALID_ID", file, citizen.getUsername()));
    }

    /** UPL-11: CITIZEN B upload cho hồ sơ CITIZEN A → lỗi ownership */
    @Test
    void upload_otherCitizenHoSo_fails() {
        MultipartFile file = pdfFile("test.pdf");
        assertThrows(BadRequestException.class,
                () -> taiLieuService.upload(hoSo.getId(), file, citizen2.getUsername()));
    }

    /** UPL-12: OFFICER upload (cho phép) */
    @Test
    void upload_officer_success() {
        MultipartFile file = pdfFile("officer_upload.pdf");
        TaiLieuDinhKem tl = taiLieuService.upload(hoSo.getId(), file, officer.getUsername());
        assertNotNull(tl.getId());
    }

    // ═══════════════════════════════════════════════════════════
    //  C. DANH SÁCH + XÓA
    // ═══════════════════════════════════════════════════════════

    /** UPL-14: Danh sách tài liệu theo hồ sơ */
    @Test
    void findByHoSo_returns_uploaded() {
        taiLieuService.upload(hoSo.getId(), pdfFile("file1.pdf"), citizen.getUsername());
        taiLieuService.upload(hoSo.getId(), pdfFile("file2.pdf"), citizen.getUsername());

        List<TaiLieuDinhKem> list = taiLieuService.findByHoSo(hoSo.getId(), citizen.getUsername());
        assertEquals(2, list.size());
    }

    /** UPL-17: Xóa tài liệu thành công */
    @Test
    void delete_success() {
        TaiLieuDinhKem tl = taiLieuService.upload(hoSo.getId(), pdfFile("test.pdf"), citizen.getUsername());
        taiLieuService.delete(tl.getId(), citizen.getUsername());
        assertFalse(taiLieuRepo.existsById(tl.getId()));
    }

    /** UPL-18: CITIZEN B xóa tài liệu CITIZEN A → lỗi */
    @Test
    void delete_otherCitizen_fails() {
        TaiLieuDinhKem tl = taiLieuService.upload(hoSo.getId(), pdfFile("test.pdf"), citizen.getUsername());
        assertThrows(BadRequestException.class,
                () -> taiLieuService.delete(tl.getId(), citizen2.getUsername()));
    }

    /** UPL-19: Chi tiết tài liệu với access check */
    @Test
    void findByIdWithAccess_otherCitizen_fails() {
        TaiLieuDinhKem tl = taiLieuService.upload(hoSo.getId(), pdfFile("test.pdf"), citizen.getUsername());
        assertThrows(BadRequestException.class,
                () -> taiLieuService.findByIdWithAccess(tl.getId(), citizen2.getUsername()));
    }

    // ═══════════════════════════════════════════════════════════
    //  D. OCR
    // ═══════════════════════════════════════════════════════════

    /** OCR-01: OCR file PDF thành công */
    @Test
    void ocr_pdf_success() {
        TaiLieuDinhKem tl = taiLieuService.upload(hoSo.getId(), pdfFile("test.pdf"), citizen.getUsername());
        var result = taiLieuService.processOcr(tl.getId());
        assertNotNull(result);
    }

    /** OCR-03: OCR file không tồn tại → lỗi */
    @Test
    void ocr_nonExistent_fails() {
        assertThrows(ResourceNotFoundException.class,
                () -> taiLieuService.processOcr("INVALID_ID"));
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════

    private MockMultipartFile pdfFile(String name) {
        return new MockMultipartFile("file", name, "application/pdf",
                "PDF content for testing".getBytes());
    }
}
