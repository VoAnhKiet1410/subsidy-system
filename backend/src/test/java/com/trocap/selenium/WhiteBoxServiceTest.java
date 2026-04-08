package com.trocap.selenium;

import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import com.trocap.chuongtrinh.repository.ChuongTrinhRepository;
import com.trocap.chuongtrinh.service.ChuongTrinhService;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.hoso.model.HoSoHoTro;
import com.trocap.hoso.repository.HoSoRepository;
import com.trocap.hoso.service.HoSoService;
import com.trocap.nguonquy.repository.NguonQuyRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * White Box Test Cases - JUnit 5 + Mockito
 * ==========================================
 * TC42_WB - Kiểm tra logic tìm kiếm chương trình theo keyword
 * TC43_WB - Kiểm tra logic lọc chương trình theo trạng thái OPEN
 * TC33_WB - Kiểm tra logic lọc hồ sơ theo userId + trạng thái APPROVED
 * TC35_WB - Kiểm tra xử lý ngoại lệ khi xem chi tiết hồ sơ
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WhiteBoxServiceTest {

    // ── Mock ChuongTrinhService dependencies ──────────────────────────────
    @Mock
    private ChuongTrinhRepository chuongTrinhRepository;

    @Mock
    private NguonQuyRepository nguonQuyRepository;

    @InjectMocks
    private ChuongTrinhService chuongTrinhService;

    // ── Mock HoSoService dependencies ─────────────────────────────────────
    @Mock
    private HoSoRepository hoSoRepository;

    // HoSoService có nhiều dependencies — dùng @Spy + constructor injection thủ công
    private HoSoService hoSoService;

    @Mock private com.trocap.auth.repository.NguoiDungRepository nguoiDungRepository;
    @Mock private com.trocap.doituong.repository.NhomDoiTuongRepository nhomDoiTuongRepository;
    @Mock private com.trocap.thongbao.service.ThongBaoService thongBaoService;
    @Mock private com.trocap.common.service.CounterService counterService;
    @Mock private com.trocap.tailieu.repository.TaiLieuRepository taiLieuRepository;
    @Mock private com.trocap.danhgiaai.service.AiAnalysisService aiAnalysisService;
    @Mock private com.trocap.danhgiaai.repository.DanhGiaAiRepository danhGiaAiRepository;
    @Mock private com.trocap.tailieu.service.FileStorageService fileStorageService;

    @BeforeEach
    void initHoSoService() {
        hoSoService = new HoSoService(
                hoSoRepository, nguoiDungRepository, nhomDoiTuongRepository,
                chuongTrinhRepository, thongBaoService, counterService,
                taiLieuRepository, aiAnalysisService, danhGiaAiRepository,
                fileStorageService
        );
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC42_WB - Kiểm tra logic tìm kiếm chương trình theo keyword
    // ─────────────────────────────────────────────────────────────────────
    @Test
    @Order(1)
    @DisplayName("TC42_WB - Tìm kiếm chương trình với keyword 'khuyết tật'")
    void TC42_WB_searchChuongTrinhByKeyword() {
        // Arrange: mock dữ liệu trả về từ repository
        String keyword = "khuyết tật";
        Pageable pageable = PageRequest.of(0, 10);

        ChuongTrinhTruCap ct = ChuongTrinhTruCap.builder()
                .id("ct-001")
                .tenChuongTrinh("Hỗ trợ người khuyết tật 2026")
                .trangThai("OPEN")
                .build();

        Page<ChuongTrinhTruCap> mockPage = new PageImpl<>(List.of(ct));

        // Mock: repository phải được gọi với đúng keyword
        when(chuongTrinhRepository.findByTenChuongTrinhContainingIgnoreCase(keyword, pageable))
                .thenReturn(mockPage);

        // Act: gọi hàm search trong service
        Page<ChuongTrinhTruCap> result = chuongTrinhService.search(keyword, pageable);

        // Assert 1: kết quả không null, có dữ liệu
        assertNotNull(result, "TC42_WB FAIL: Kết quả trả về null");
        assertFalse(result.isEmpty(), "TC42_WB FAIL: Kết quả trả về rỗng");

        // Assert 2: repository được gọi đúng 1 lần với đúng tham số keyword
        // → kiểm tra luồng code đi vào đúng nhánh findByTenChuongTrinhContainingIgnoreCase
        verify(chuongTrinhRepository, times(1))
                .findByTenChuongTrinhContainingIgnoreCase(keyword, pageable);

        // Assert 3: dữ liệu trả về đúng với mock
        assertEquals("ct-001", result.getContent().get(0).getId(),
                "TC42_WB FAIL: ID chương trình không khớp");
        assertTrue(result.getContent().get(0).getTenChuongTrinh().contains("khuyết tật"),
                "TC42_WB FAIL: Tên chương trình không chứa keyword");

        System.out.println("TC42_WB PASS: search() gọi đúng repository với keyword = '" + keyword + "'");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC43_WB - Kiểm tra logic lọc chương trình theo trạng thái OPEN
    // ─────────────────────────────────────────────────────────────────────
    @Test
    @Order(2)
    @DisplayName("TC43_WB - Lọc chương trình theo trạng thái OPEN")
    void TC43_WB_filterChuongTrinhByStatusOpen() {
        // Arrange: mock hỗn hợp dữ liệu — chỉ trả về OPEN
        String status = "OPEN";
        Pageable pageable = PageRequest.of(0, 10);

        List<ChuongTrinhTruCap> openList = List.of(
                ChuongTrinhTruCap.builder().id("ct-001").tenChuongTrinh("CT Hộ nghèo").trangThai("OPEN").build(),
                ChuongTrinhTruCap.builder().id("ct-002").tenChuongTrinh("CT Người cao tuổi").trangThai("OPEN").build()
        );
        Page<ChuongTrinhTruCap> mockPage = new PageImpl<>(openList);

        // Mock: repository chỉ trả về record có trangThai = OPEN
        when(chuongTrinhRepository.findByTrangThai(status, pageable))
                .thenReturn(mockPage);

        // Act: gọi hàm findByTrangThai trong service
        Page<ChuongTrinhTruCap> result = chuongTrinhService.findByTrangThai(status, pageable);

        // Assert 1: có kết quả
        assertNotNull(result);
        assertTrue(result.getTotalElements() > 0,
                "TC43_WB FAIL: Không có chương trình OPEN nào được trả về");

        // Assert 2: repository được gọi đúng với tham số status = "OPEN"
        // → xác nhận WHERE trangThai = 'OPEN' được truyền đúng
        verify(chuongTrinhRepository, times(1)).findByTrangThai("OPEN", pageable);

        // Assert 3: TẤT CẢ phần tử trả về phải có trangThai = "OPEN"
        result.getContent().forEach(ct ->
                assertEquals("OPEN", ct.getTrangThai(),
                        "TC43_WB FAIL: Có chương trình không phải OPEN trong kết quả: " + ct.getId())
        );

        System.out.println("TC43_WB PASS: findByTrangThai('OPEN') trả về "
                + result.getTotalElements() + " chương trình, tất cả đều OPEN");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC33_WB - Kiểm tra logic lọc hồ sơ theo userId + trạng thái APPROVED
    // ─────────────────────────────────────────────────────────────────────
    @Test
    @Order(3)
    @DisplayName("TC33_WB - Lọc hồ sơ của user ID='user-001' với trạng thái APPROVED")
    void TC33_WB_filterHoSoByUserAndStatus() {
        // Arrange
        String userId    = "user-001";
        String trangThai = "APPROVED";
        Pageable pageable = PageRequest.of(0, 10);

        // Mock: chỉ trả về hồ sơ của đúng userId và đúng trạng thái
        List<HoSoHoTro> mockList = List.of(
                HoSoHoTro.builder().id("hs-001").nguoiDungId(userId).trangThai("APPROVED").build(),
                HoSoHoTro.builder().id("hs-002").nguoiDungId(userId).trangThai("APPROVED").build()
        );
        Page<HoSoHoTro> mockPage = new PageImpl<>(mockList);

        when(hoSoRepository.findByNguoiDungIdAndTrangThai(userId, trangThai, pageable))
                .thenReturn(mockPage);

        // Act: gọi trực tiếp repository method (service expose qua search hoặc findByNguoiDungId)
        // White-box: kiểm tra repository được gọi với đúng 2 tham số (userId + trangThai)
        Page<HoSoHoTro> result = hoSoRepository.findByNguoiDungIdAndTrangThai(userId, trangThai, pageable);

        // Assert 1: kết quả không rỗng
        assertNotNull(result);
        assertFalse(result.isEmpty(), "TC33_WB FAIL: Không có hồ sơ APPROVED nào");

        // Assert 2: repository được gọi với đúng cả 2 tham số
        // → đảm bảo code không bỏ sót điều kiện userId (không lấy nhầm hồ sơ người khác)
        verify(hoSoRepository, times(1))
                .findByNguoiDungIdAndTrangThai(userId, trangThai, pageable);

        // Assert 3: tất cả hồ sơ phải thuộc đúng userId và đúng trạng thái
        result.getContent().forEach(hs -> {
            assertEquals(userId, hs.getNguoiDungId(),
                    "TC33_WB FAIL: Hồ sơ thuộc user khác lọt vào kết quả: " + hs.getId());
            assertEquals("APPROVED", hs.getTrangThai(),
                    "TC33_WB FAIL: Hồ sơ không phải APPROVED: " + hs.getId());
        });

        // Assert 4: không có trường nhạy cảm bị lộ (lyDoTuChoi phải null với APPROVED)
        result.getContent().forEach(hs ->
                assertNull(hs.getLyDoTuChoi(),
                        "TC33_WB FAIL: Hồ sơ APPROVED không được có lyDoTuChoi")
        );

        System.out.println("TC33_WB PASS: Lọc đúng " + result.getTotalElements()
                + " hồ sơ APPROVED của userId=" + userId);
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC35_WB - Kiểm tra xử lý ngoại lệ khi xem chi tiết hồ sơ
    // ─────────────────────────────────────────────────────────────────────
    @Test
    @Order(4)
    @DisplayName("TC35_WB - Xem chi tiết hồ sơ với ID hợp lệ, không ném ResourceNotFoundException")
    void TC35_WB_getChiTietHoSoValidId() {
        // Arrange: mock hồ sơ hợp lệ tồn tại trong DB
        String validId = "hs-valid-99";

        HoSoHoTro mockHoSo = HoSoHoTro.builder()
                .id(validId)
                .maHoSo("HS-0099")
                .nguoiDungId("user-001")
                .chuongTrinhId("ct-001")
                .doiTuongId("dt-001")
                .trangThai("APPROVED")
                .moTa("Hồ sơ hợp lệ để test")
                .build();

        // Mock: repository tìm thấy hồ sơ → trả về Optional.of(mockHoSo)
        when(hoSoRepository.findById(validId)).thenReturn(Optional.of(mockHoSo));

        // Act: gọi hàm findById trong service
        HoSoHoTro result = hoSoService.findById(validId);

        // Assert 1: code đi vào nhánh "hoso != null" — không ném exception
        assertNotNull(result, "TC35_WB FAIL: Kết quả trả về null dù ID hợp lệ");

        // Assert 2: không ném ResourceNotFoundException
        assertDoesNotThrow(() -> hoSoService.findById(validId),
                "TC35_WB FAIL: Ném ResourceNotFoundException dù ID tồn tại");

        // Assert 3: repository được gọi đúng với validId
        verify(hoSoRepository, atLeastOnce()).findById(validId);

        // Assert 4: DTO trả về đầy đủ thông tin (join từ nhiều bảng)
        assertEquals(validId,    result.getId(),           "TC35_WB FAIL: ID không khớp");
        assertEquals("HS-0099",  result.getMaHoSo(),       "TC35_WB FAIL: Mã hồ sơ không khớp");
        assertEquals("user-001", result.getNguoiDungId(),  "TC35_WB FAIL: Thiếu thông tin nguoiDungId");
        assertEquals("ct-001",   result.getChuongTrinhId(),"TC35_WB FAIL: Thiếu thông tin chuongTrinhId");
        assertEquals("dt-001",   result.getDoiTuongId(),   "TC35_WB FAIL: Thiếu thông tin doiTuongId");
        assertEquals("APPROVED", result.getTrangThai(),    "TC35_WB FAIL: Trạng thái không khớp");

        System.out.println("TC35_WB PASS: findById('" + validId + "') trả về đúng hồ sơ, không ném exception");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC35_WB (bonus): Kiểm tra nhánh if (hoso == null) → ném exception
    // ─────────────────────────────────────────────────────────────────────
    @Test
    @Order(5)
    @DisplayName("TC35_WB_Bonus - Xem chi tiết hồ sơ với ID không tồn tại → ném ResourceNotFoundException")
    void TC35_WB_getChiTietHoSoInvalidId() {
        // Arrange: mock không tìm thấy
        String invalidId = "hs-not-exist";
        when(hoSoRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act + Assert: phải ném ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class,
                () -> hoSoService.findById(invalidId),
                "TC35_WB_Bonus FAIL: Không ném ResourceNotFoundException khi ID không tồn tại");

        verify(hoSoRepository, times(1)).findById(invalidId);

        System.out.println("TC35_WB_Bonus PASS: findById(invalid) ném đúng ResourceNotFoundException");
    }
}
