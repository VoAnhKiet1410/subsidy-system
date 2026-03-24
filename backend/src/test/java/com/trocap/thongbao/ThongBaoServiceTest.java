package com.trocap.thongbao;

import com.trocap.TestDataFactory;
import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.thongbao.dto.ThongBaoRequest;
import com.trocap.thongbao.model.ThongBao;
import com.trocap.thongbao.repository.ThongBaoRepository;
import com.trocap.thongbao.service.ThongBaoService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test nghiệp vụ Thông báo.
 * Ref: docs/test-cases-nghiep-vu.md (#85-95)
 */
@SpringBootTest
@ActiveProfiles("test")
class ThongBaoServiceTest {

    @Autowired ThongBaoService thongBaoService;
    @Autowired ThongBaoRepository thongBaoRepo;
    @Autowired NguoiDungRepository userRepo;

    NguoiDung citizen, citizen2;

    @BeforeEach
    void setup() {
        thongBaoRepo.deleteAll();
        userRepo.deleteAll();
        citizen = userRepo.save(TestDataFactory.citizen());
        citizen2 = userRepo.save(TestDataFactory.citizen2());
    }

    // ═══════════════════════════════════════════════════════════
    //  A. TẠO THÔNG BÁO
    // ═══════════════════════════════════════════════════════════

    /** Tạo thông báo thủ công (ADMIN) */
    @Test
    void create_manual() {
        ThongBaoRequest req = new ThongBaoRequest();
        req.setNguoiDungId(citizen.getId());
        req.setTieuDe("Thông báo test");
        req.setNoiDung("Nội dung test");
        req.setLoai("INFO");

        ThongBao tb = thongBaoService.create(req);
        assertNotNull(tb.getId());
        assertEquals(false, tb.isDaDoc());
        assertEquals("INFO", tb.getLoai());
    }

    /** Tạo thông báo tự động (system gọi) */
    @Test
    void notify_systemCall() {
        thongBaoService.notify(citizen.getId(), "SUCCESS",
                "Hồ sơ được duyệt", "Chi tiết...",
                "APPLICATION", "hs-01", "/applications/hs-01");

        long count = thongBaoRepo.countByNguoiDungIdAndDaDoc(citizen.getId(), false);
        assertEquals(1, count);
    }

    // ═══════════════════════════════════════════════════════════
    //  B. DANH SÁCH + FILTER
    // ═══════════════════════════════════════════════════════════

    /** Lấy danh sách thông báo theo userId */
    @Test
    void findByUser_returns_onlyOwnNotifications() {
        createTestNotifications();

        Page<ThongBao> citizenPage = thongBaoService.findByNguoiDungId(citizen.getId(), null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
        assertEquals(3, citizenPage.getTotalElements());

        Page<ThongBao> citizen2Page = thongBaoService.findByNguoiDungId(citizen2.getId(), null,
                PageRequest.of(0, 10));
        assertEquals(1, citizen2Page.getTotalElements());
    }

    /** Filter daDoc = false */
    @Test
    void findByUser_filterUnread() {
        createTestNotifications();

        Page<ThongBao> unread = thongBaoService.findByNguoiDungId(citizen.getId(), false,
                PageRequest.of(0, 10));
        assertEquals(2, unread.getTotalElements());
    }

    /** Filter daDoc = true */
    @Test
    void findByUser_filterRead() {
        createTestNotifications();

        Page<ThongBao> read = thongBaoService.findByNguoiDungId(citizen.getId(), true,
                PageRequest.of(0, 10));
        assertEquals(1, read.getTotalElements());
    }

    // ═══════════════════════════════════════════════════════════
    //  C. MARK READ + DELETE
    // ═══════════════════════════════════════════════════════════

    /** Đánh dấu đã đọc */
    @Test
    void markRead_success() {
        ThongBao tb = createNotification(citizen.getId(), false);
        ThongBao updated = thongBaoService.markRead(tb.getId());
        assertTrue(updated.isDaDoc());
    }

    /** Đếm chưa đọc */
    @Test
    void countUnread() {
        createTestNotifications();
        long count = thongBaoService.countUnread(citizen.getId());
        assertEquals(2, count);
    }

    /** Xóa thông báo */
    @Test
    void delete_success() {
        ThongBao tb = createNotification(citizen.getId(), false);
        thongBaoService.delete(tb.getId());
        assertFalse(thongBaoRepo.existsById(tb.getId()));
    }

    /** Xóa thông báo không tồn tại → lỗi */
    @Test
    void delete_notFound_fails() {
        assertThrows(ResourceNotFoundException.class,
                () -> thongBaoService.delete("INVALID_ID"));
    }

    /** findById không tồn tại → lỗi */
    @Test
    void findById_notFound_fails() {
        assertThrows(ResourceNotFoundException.class,
                () -> thongBaoService.findById("INVALID_ID"));
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════

    private void createTestNotifications() {
        // Citizen: 3 thông báo (2 chưa đọc, 1 đã đọc)
        createNotification(citizen.getId(), false);
        createNotification(citizen.getId(), false);
        createNotification(citizen.getId(), true);
        // Citizen2: 1 thông báo
        createNotification(citizen2.getId(), false);
    }

    private ThongBao createNotification(String userId, boolean daDoc) {
        return thongBaoRepo.save(ThongBao.builder()
                .nguoiDungId(userId)
                .tieuDe("Test notification")
                .noiDung("Content")
                .loai("INFO")
                .daDoc(daDoc)
                .build());
    }
}
