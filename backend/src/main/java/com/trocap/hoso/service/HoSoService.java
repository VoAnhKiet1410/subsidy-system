package com.trocap.hoso.service;

import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.chuongtrinh.repository.ChuongTrinhRepository;
import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.common.service.CounterService;
import com.trocap.doituong.repository.NhomDoiTuongRepository;
import com.trocap.hoso.dto.HoSoRequest;
import com.trocap.hoso.dto.RejectRequest;
import com.trocap.hoso.model.HoSoHoTro;
import com.trocap.hoso.repository.HoSoRepository;
import com.trocap.thongbao.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import com.trocap.tailieu.repository.TaiLieuRepository;
import com.trocap.tailieu.model.TaiLieuDinhKem;
import com.trocap.danhgiaai.model.DanhGiaAi;
import com.trocap.danhgiaai.repository.DanhGiaAiRepository;
import com.trocap.danhgiaai.service.AiAnalysisService;
import com.trocap.danhgiaai.dto.AiAnalysisResult;
import com.trocap.tailieu.service.FileStorageService;
import org.springframework.scheduling.annotation.Async;

@Service
@RequiredArgsConstructor
public class HoSoService {

    private final HoSoRepository hoSoRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final NhomDoiTuongRepository nhomDoiTuongRepository;
    private final ChuongTrinhRepository chuongTrinhRepository;
    private final ThongBaoService thongBaoService;
    private final CounterService counterService;
    private final TaiLieuRepository taiLieuRepository;
    private final AiAnalysisService aiAnalysisService;
    private final DanhGiaAiRepository danhGiaAiRepository;
    private final FileStorageService fileStorageService;

    // ─── Danh sách (ADMIN/OFFICER xem tất cả) ──────────────────────
    public Page<HoSoHoTro> findAll(Pageable pageable) {
        return hoSoRepository.findAll(pageable);
    }

    // ─── Tìm kiếm nâng cao (Criteria API, nhiều filter đồng thời) ───
    public Page<HoSoHoTro> search(String nguoiDungId, String chuongTrinhId,
                                   String doiTuongId, String trangThai,
                                   LocalDate fromDate, LocalDate toDate,
                                   Pageable pageable) {
        return hoSoRepository.search(nguoiDungId, chuongTrinhId, doiTuongId,
                trangThai, fromDate, toDate, pageable);
    }

    // ─── Chi tiết ───────────────────────────────────────────────────
    public HoSoHoTro findById(String id) {
        return hoSoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ: " + id));
    }

    // ─── Lọc theo người nộp (CITIZEN xem hồ sơ của mình) ───────────
    public Page<HoSoHoTro> findByNguoiDungId(String nguoiDungId, Pageable pageable) {
        return hoSoRepository.findByNguoiDungId(nguoiDungId, pageable);
    }

    // ─── Lọc theo trạng thái ────────────────────────────────────────
    public Page<HoSoHoTro> findByTrangThai(String trangThai, Pageable pageable) {
        return hoSoRepository.findByTrangThai(trangThai, pageable);
    }

    // ─── Lọc theo chương trình ──────────────────────────────────────
    public Page<HoSoHoTro> findByChuongTrinhId(String chuongTrinhId, Pageable pageable) {
        return hoSoRepository.findByChuongTrinhId(chuongTrinhId, pageable);
    }

    // ─── Lọc theo khoảng ngày nộp ───────────────────────────────────
    public Page<HoSoHoTro> findByNgayNopRange(LocalDate from, LocalDate to, Pageable pageable) {
        return hoSoRepository.findByNgayNopRange(from, to, pageable);
    }

    // ─── Lấy thống kê cá nhân ─────────────────────────────────────────
    public java.util.Map<String, Long> getPersonalStats(String username) {
        NguoiDung user = nguoiDungRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));

        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("total", hoSoRepository.countByNguoiDungId(user.getId()));
        stats.put("pending", hoSoRepository.countByNguoiDungIdAndTrangThai(user.getId(), "UNDER_REVIEW") 
                           + hoSoRepository.countByNguoiDungIdAndTrangThai(user.getId(), "SUBMITTED"));
        stats.put("approved", hoSoRepository.countByNguoiDungIdAndTrangThai(user.getId(), "APPROVED")
                            + hoSoRepository.countByNguoiDungIdAndTrangThai(user.getId(), "PAID"));
        
        return stats;
    }

    // ─── Tạo hồ sơ mới (trạng thái DRAFT) ──────────────────────────
    public HoSoHoTro create(HoSoRequest request, String username) {
        // Lấy userId từ username
        NguoiDung user = nguoiDungRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));

        // Validate references
        validateReferences(request);

        // Sinh mã hồ sơ tuần tự: HS-0001, HS-0002, ...
        long seq = counterService.getNextSequence("hoso_seq");
        String maHoSo = String.format("HS-%04d", seq);

        HoSoHoTro hoSo = HoSoHoTro.builder()
                .maHoSo(maHoSo)
                .nguoiDungId(user.getId())
                .doiTuongId(request.getDoiTuongId())
                .chuongTrinhId(request.getChuongTrinhId())
                .moTa(request.getMoTa())
                .soTienDeXuat(request.getSoTienDeXuat())
                .trangThai("DRAFT")  // Mặc định DRAFT khi mới tạo
                .build();

        return hoSoRepository.save(hoSo);
    }

    // ─── Cập nhật hồ sơ (chỉ khi DRAFT) ────────────────────────────
    public HoSoHoTro update(String id, HoSoRequest request, String username) {
        HoSoHoTro hoSo = findById(id);

        // Chỉ cho cập nhật khi DRAFT
        if (!"DRAFT".equals(hoSo.getTrangThai())) {
            throw new BadRequestException("Chỉ có thể cập nhật hồ sơ ở trạng thái DRAFT");
        }

        // CITIZEN chỉ sửa hồ sơ của mình
        verifyOwnership(hoSo, username);

        // Validate references
        validateReferences(request);

        if (request.getDoiTuongId() != null) hoSo.setDoiTuongId(request.getDoiTuongId());
        if (request.getChuongTrinhId() != null) hoSo.setChuongTrinhId(request.getChuongTrinhId());
        if (request.getMoTa() != null) hoSo.setMoTa(request.getMoTa());
        if (request.getSoTienDeXuat() != null) hoSo.setSoTienDeXuat(request.getSoTienDeXuat());

        return hoSoRepository.save(hoSo);
    }

    // ─── Xóa hồ sơ (chỉ khi DRAFT) ─────────────────────────────────
    public void delete(String id, String username) {
        HoSoHoTro hoSo = findById(id);

        if (!"DRAFT".equals(hoSo.getTrangThai())) {
            throw new BadRequestException("Chỉ có thể xóa hồ sơ ở trạng thái DRAFT");
        }

        verifyOwnership(hoSo, username);
        hoSoRepository.deleteById(id);
    }

    // ─── Submit hồ sơ (DRAFT → SUBMITTED) ───────────────────────────
    public HoSoHoTro submit(String id, String username) {
        HoSoHoTro hoSo = findById(id);

        // Chỉ submit từ DRAFT
        if (!"DRAFT".equals(hoSo.getTrangThai())) {
            throw new BadRequestException("Chỉ có thể nộp hồ sơ ở trạng thái DRAFT. Hiện tại: " + hoSo.getTrangThai());
        }

        // CITIZEN chỉ submit hồ sơ của mình
        verifyOwnership(hoSo, username);

        // Kiểm tra thông tin bắt buộc trước khi submit
        if (hoSo.getDoiTuongId() == null || hoSo.getDoiTuongId().isBlank()) {
            throw new BadRequestException("Thiếu thông tin đối tượng hưởng trợ cấp");
        }
        if (hoSo.getChuongTrinhId() == null || hoSo.getChuongTrinhId().isBlank()) {
            throw new BadRequestException("Thiếu thông tin chương trình trợ cấp");
        }

        ChuongTrinhTruCap chuongTrinh = chuongTrinhRepository.findById(hoSo.getChuongTrinhId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương trình trợ cấp"));

        // Validate Giấy tờ bắt buộc theo cấu hình của Chương Trình (BƯỚC 3)
        List<TaiLieuDinhKem> dsTaiLieuDaNop = taiLieuRepository.findByHoSoHoTroId(id);
        
        Set<String> cacLoaiGiayToDaNop = dsTaiLieuDaNop.stream()
                .map(TaiLieuDinhKem::getLoaiGiayTo)
                .filter(loai -> loai != null)
                .collect(Collectors.toSet());

        List<String> giayToBatBuoc = chuongTrinh.getRequiredDocuments();
        if (giayToBatBuoc != null && !giayToBatBuoc.isEmpty()) {
            List<String> giayToThieu = new ArrayList<>();
            for (String doc : giayToBatBuoc) {
                if (!cacLoaiGiayToDaNop.contains(doc)) {
                    giayToThieu.add(doc);
                }
            }
            if (!giayToThieu.isEmpty()) {
                throw new BadRequestException("Hồ sơ chưa hợp lệ. Bạn cần nộp thêm loại giấy tờ: " + String.join(", ", giayToThieu));
            }
        }

        hoSo.setTrangThai("SUBMITTED");
        hoSo.setNgayNopHoSo(LocalDate.now());

        HoSoHoTro savedHoSo = hoSoRepository.save(hoSo);

        // Chạy bất đồng bộ Đánh giá AI (BƯỚC 4 & BƯỚC 5)
        tienHanhDanhGiaAi(savedHoSo, dsTaiLieuDaNop);

        return savedHoSo;
    }

    /**
     * Chạy ngầm phân tích AI sau khi hồ sơ duyệt
     */
    @Async
    public void tienHanhDanhGiaAi(HoSoHoTro hoSo, List<TaiLieuDinhKem> dsTaiLieu) {
        try {
            // Tìm Form Mẫu đã điền. Bạn có thể thay string theo quy ước FE.
            TaiLieuDinhKem formMaus = dsTaiLieu.stream()
                    .filter(doc -> "Form Đăng Ký".equalsIgnoreCase(doc.getLoaiGiayTo()) || doc.getLoaiGiayTo() != null && doc.getLoaiGiayTo().contains("Form"))
                    .findFirst().orElse(null);

            if (formMaus != null && formMaus.getDuongDanFile() != null) {
                Path filePath = fileStorageService.getFilePath(formMaus.getDuongDanFile());
                byte[] fileBytes = Files.readAllBytes(filePath);

                // Gọi mô hình Python AI thông qua REST (AiAnalysisService)
                AiAnalysisResult result = aiAnalysisService.analyzeDocumentBytes(fileBytes, formMaus.getTenTaiLieu());

                if (result != null) {
                    // Logic đối chiếu 
                    int diemTinCay = (int) Math.round(result.getOcrConfidence() * 100);
                    String ruiRo = result.getIsTampered() ? "HIGH" : (diemTinCay < 50 ? "MEDIUM" : "LOW");

                    Map<String, Object> chiTiet = new HashMap<>();
                    chiTiet.put("ocr_text", result.getOcrText());
                    chiTiet.put("tampering_score", result.getTamperingScore());
                    chiTiet.put("is_tampered", result.getIsTampered());

                    DanhGiaAi danhGiaAi = DanhGiaAi.builder()
                            .hoSoId(hoSo.getId())
                            .diemTinCay(diemTinCay)
                            .mucDoRuiRo(ruiRo)
                            .chiTietPhanTich(chiTiet)
                            .danhGiaBoi("AI_MODEL_EASYOCR_ELA")
                            .trangThai("COMPLETED")
                            .build();

                    danhGiaAiRepository.save(danhGiaAi);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi quá trình AI phân tích hồ sơ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // WORKFLOW: DUYỆT / TỪ CHỐI HỒ SƠ
    // ═══════════════════════════════════════════════════════════════

    // ─── Bắt đầu xét duyệt (SUBMITTED/REJECTED → UNDER_REVIEW) ─────
    public HoSoHoTro startReview(String id, String username) {
        HoSoHoTro hoSo = findById(id);

        // Cho phép chuyển từ SUBMITTED hoặc REJECTED (re-review sau khi bổ sung)
        if (!"SUBMITTED".equals(hoSo.getTrangThai())
                && !"REJECTED".equals(hoSo.getTrangThai())) {
            throw new BadRequestException(
                    "Chỉ hồ sơ ở trạng thái SUBMITTED hoặc REJECTED mới được chuyển sang UNDER_REVIEW. "
                    + "Trạng thái hiện tại: " + hoSo.getTrangThai());
        }

        hoSo.setTrangThai("UNDER_REVIEW");
        hoSo.setReviewedBy(username);
        HoSoHoTro saved = hoSoRepository.save(hoSo);

        // Thông báo cho người nộp hồ sơ
        thongBaoService.notify(
                hoSo.getNguoiDungId(),
                "INFO",
                "Hồ sơ đang được xét duyệt",
                "Hồ sơ #" + hoSo.getId() + " của bạn đang được cán bộ xem xét.",
                "APPLICATION", hoSo.getId(),
                "/applications/" + hoSo.getId()
        );

        return saved;
    }

    // ─── Phê duyệt (UNDER_REVIEW → APPROVED) ───────────────────────
    public HoSoHoTro approve(String id, String username) {
        HoSoHoTro hoSo = findById(id);

        if (!"UNDER_REVIEW".equals(hoSo.getTrangThai())) {
            throw new BadRequestException(
                    "Chỉ hồ sơ ở trạng thái UNDER_REVIEW mới được phê duyệt. "
                    + "Trạng thái hiện tại: " + hoSo.getTrangThai());
        }

        hoSo.setTrangThai("APPROVED");
        hoSo.setApprovedBy(username);
        hoSo.setApprovedAt(LocalDateTime.now());
        hoSo.setLyDoTuChoi(null);  // Xóa lý do từ chối cũ nếu có
        HoSoHoTro saved = hoSoRepository.save(hoSo);

        // Thông báo SUCCESS cho người nộp hồ sơ
        thongBaoService.notify(
                hoSo.getNguoiDungId(),
                "SUCCESS",
                "Hồ sơ đã được phê duyệt",
                "Hồ sơ #" + hoSo.getId() + " của bạn đã được phê duyệt bởi " + username + ".",
                "APPLICATION", hoSo.getId(),
                "/applications/" + hoSo.getId()
        );

        return saved;
    }

    // ─── Từ chối (UNDER_REVIEW → REJECTED) ──────────────────────────
    public HoSoHoTro reject(String id, RejectRequest request, String username) {
        HoSoHoTro hoSo = findById(id);

        if (!"UNDER_REVIEW".equals(hoSo.getTrangThai())) {
            throw new BadRequestException(
                    "Chỉ hồ sơ ở trạng thái UNDER_REVIEW mới được từ chối. "
                    + "Trạng thái hiện tại: " + hoSo.getTrangThai());
        }

        // Validate lý do từ chối (defense in depth — controller cũng validate qua @Valid)
        if (request.getLyDoTuChoi() == null || request.getLyDoTuChoi().isBlank()) {
            throw new BadRequestException("Lý do từ chối không được để trống");
        }

        hoSo.setTrangThai("REJECTED");
        hoSo.setLyDoTuChoi(request.getLyDoTuChoi());
        HoSoHoTro saved = hoSoRepository.save(hoSo);

        // Thông báo WARNING cho người nộp hồ sơ
        thongBaoService.notify(
                hoSo.getNguoiDungId(),
                "WARNING",
                "Hồ sơ bị từ chối",
                "Hồ sơ #" + hoSo.getId() + " của bạn đã bị từ chối. Lý do: " + request.getLyDoTuChoi(),
                "APPLICATION", hoSo.getId(),
                "/applications/" + hoSo.getId()
        );

        return saved;
    }

    // ─── Helper: validate references ────────────────────────────────
    private void validateReferences(HoSoRequest request) {
        if (request.getDoiTuongId() != null
                && !nhomDoiTuongRepository.existsById(request.getDoiTuongId())) {
            throw new BadRequestException("Nhóm đối tượng hưởng trợ cấp không tồn tại: " + request.getDoiTuongId());
        }
        if (request.getChuongTrinhId() != null
                && !chuongTrinhRepository.existsById(request.getChuongTrinhId())) {
            throw new BadRequestException("Chương trình trợ cấp không tồn tại: " + request.getChuongTrinhId());
        }
    }

    // ─── Helper: CITIZEN chỉ thao tác hồ sơ của mình ────────────────
    private void verifyOwnership(HoSoHoTro hoSo, String username) {
        NguoiDung user = nguoiDungRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));

        // ADMIN/OFFICER bỏ qua check ownership
        if (user.getRoles() != null
                && (user.getRoles().contains("ADMIN") || user.getRoles().contains("OFFICER"))) {
            return;
        }

        // CITIZEN chỉ sửa/xóa/submit hồ sơ của mình
        if (!user.getId().equals(hoSo.getNguoiDungId())) {
            throw new BadRequestException("Bạn không có quyền thao tác hồ sơ này");
        }
    }
}
