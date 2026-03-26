package com.trocap.chitra.service;

import com.trocap.chitra.dto.PaymentRequest;
import com.trocap.chitra.dto.PaymentStatusRequest;
import com.trocap.chitra.model.ChiTraTruCap;
import com.trocap.chitra.repository.ChiTraRepository;
import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import com.trocap.chuongtrinh.repository.ChuongTrinhRepository;
import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.hoso.model.HoSoHoTro;
import com.trocap.hoso.repository.HoSoRepository;
import com.trocap.nguonquy.model.NguonQuy;
import com.trocap.nguonquy.repository.NguonQuyRepository;
import com.trocap.thongbao.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChiTraService {

    private final ChiTraRepository chiTraRepository;
    private final HoSoRepository hoSoRepository;
    private final ChuongTrinhRepository chuongTrinhRepository;
    private final NguonQuyRepository nguonQuyRepository;
    private final ThongBaoService thongBaoService;

    private static final Set<String> VALID_STATUSES = Set.of("PENDING", "SUCCESS", "FAILED", "CANCELLED");

    // ─── Danh sách (phân trang + lọc trạng thái) ────────────────────
    public Page<ChiTraTruCap> findAll(Pageable pageable) {
        return chiTraRepository.findAll(pageable);
    }

    public Page<ChiTraTruCap> findByTrangThai(String trangThai, Pageable pageable) {
        return chiTraRepository.findByTrangThai(trangThai, pageable);
    }

    // ─── Chi tiết ───────────────────────────────────────────────────
    public ChiTraTruCap findById(String id) {
        return chiTraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch: " + id));
    }

    // ─── Tìm chi trả theo hồ sơ ─────────────────────────────────────
    public List<ChiTraTruCap> findByHoSoId(String hoSoId) {
        return chiTraRepository.findByHoSoHoTroId(hoSoId);
    }

    // ─── Tạo chi trả mới (PENDING) ─────────────────────────────────
    @PreAuthorize("hasRole('OFFICER')")
    public ChiTraTruCap create(PaymentRequest request, String username) {
        // 1. Validate hồ sơ tồn tại
        HoSoHoTro hoSo = hoSoRepository.findById(request.getHoSoHoTroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy hồ sơ: " + request.getHoSoHoTroId()));

        // 2. Chỉ tạo payment cho hồ sơ đã APPROVED
        if (!"APPROVED".equals(hoSo.getTrangThai())) {
            throw new BadRequestException(
                    "Chỉ tạo chi trả cho hồ sơ đã được phê duyệt (APPROVED). "
                    + "Trạng thái hiện tại: " + hoSo.getTrangThai());
        }

        // 3. Tạo payment với trạng thái PENDING
        ChiTraTruCap payment = ChiTraTruCap.builder()
                .hoSoHoTroId(request.getHoSoHoTroId())
                .soTien(request.getSoTien())
                .phuongThuc(request.getPhuongThuc())
                .ghiChu(request.getGhiChu())
                .trangThai("PENDING")
                .ngayChiTra(LocalDate.now())
                .processedBy(username)
                .build();

        payment = chiTraRepository.save(payment);

        hoSo.setTrangThaiChiTra("PROCESSING");
        hoSoRepository.save(hoSo);

        return payment;
    }

    // ─── Cập nhật trạng thái chi trả ────────────────────────────────
    public ChiTraTruCap updateStatus(String id, PaymentStatusRequest request, String username) {
    ChiTraTruCap payment = findById(id);

    // Validate trạng thái hợp lệ
    if (!VALID_STATUSES.contains(request.getTrangThai())) {
        throw new BadRequestException(
                "Trạng thái không hợp lệ: " + request.getTrangThai()
                        + ". Hợp lệ: " + VALID_STATUSES);
    }

    // Không cho sửa payment đã kết thúc
    if ("SUCCESS".equals(payment.getTrangThai())) {
        throw new BadRequestException("Không thể thay đổi trạng thái giao dịch đã thành công");
    }
    if ("CANCELLED".equals(payment.getTrangThai())) {
        throw new BadRequestException("Không thể thay đổi trạng thái giao dịch đã huỷ");
    }
    if ("FAILED".equals(payment.getTrangThai())) {
        throw new BadRequestException("Không thể thay đổi trạng thái giao dịch đã thất bại. Vui lòng tạo giao dịch mới.");
    }

    String newStatus = request.getTrangThai();
    payment.setTrangThai(newStatus);
    payment.setProcessedBy(username);

    // ===== Xử lý side-effects =====
    if ("SUCCESS".equals(newStatus)) {
        processSuccessPayment(payment);

    } else if ("FAILED".equals(newStatus) || "CANCELLED".equals(newStatus)) {
        HoSoHoTro hoSo = hoSoRepository.findById(payment.getHoSoHoTroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy hồ sơ: " + payment.getHoSoHoTroId()));

        hoSo.setTrangThaiChiTra("FAILED");
        hoSoRepository.save(hoSo);
    }

    return chiTraRepository.save(payment);
}

    // ─── Logic xử lý khi payment SUCCESS ────────────────────────────
    private void processSuccessPayment(ChiTraTruCap payment) {
        // 1. Lấy hồ sơ
        HoSoHoTro hoSo = hoSoRepository.findById(payment.getHoSoHoTroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy hồ sơ: " + payment.getHoSoHoTroId()));

        // 2. Lấy chương trình → nguồn quỹ
        ChuongTrinhTruCap chuongTrinh = chuongTrinhRepository.findById(hoSo.getChuongTrinhId())
                .orElse(null);

        // 3. Trừ ngân sách nguồn quỹ (nếu có liên kết)
        if (chuongTrinh != null && chuongTrinh.getNguonQuyId() != null) {
            deductBudget(chuongTrinh.getNguonQuyId(), chuongTrinh, payment.getSoTien());
        }

        // 4. Cập nhật hồ sơ sang PAID và trạng thái chi trả COMPLETED
        hoSo.setTrangThai("PAID");
        hoSo.setTrangThaiChiTra("COMPLETED");
        hoSoRepository.save(hoSo);
        log.info("Hồ sơ {} đã chuyển sang PAID", hoSo.getId());

        // 5. Tạo thông báo cho người dùng
        thongBaoService.notify(
                hoSo.getNguoiDungId(),
                "SUCCESS",
                "Chi trả trợ cấp thành công",
                String.format("Hồ sơ #%s đã được chi trả %,.0fđ qua %s.",
                        hoSo.getId(), payment.getSoTien(), payment.getPhuongThuc()),
                "PAYMENT", payment.getId(),
                "/applications/" + hoSo.getId()
        );
    }

    // ─── Trừ ngân sách nguồn quỹ ────────────────────────────────────
    private void deductBudget(String nguonQuyId, ChuongTrinhTruCap chuongTrinh, Double soTien) {
        NguonQuy nguonQuy = nguonQuyRepository.findById(nguonQuyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy nguồn quỹ: " + nguonQuyId));

        double conLai = nguonQuy.getConLai() != null ? nguonQuy.getConLai() : 0.0;

        // Kiểm tra không vượt ngân sách
        if (soTien > conLai) {
            throw new BadRequestException(
                    String.format("Ngân sách nguồn quỹ '%s' không đủ. Còn lại: %,.0fđ, cần: %,.0fđ",
                            nguonQuy.getTenNguonQuy(), conLai, soTien));
        }

        // Trừ nguồn quỹ
        nguonQuy.setDaSuDung((nguonQuy.getDaSuDung() != null ? nguonQuy.getDaSuDung() : 0.0) + soTien);
        nguonQuy.setConLai(conLai - soTien);
        nguonQuyRepository.save(nguonQuy);
        log.info("Nguồn quỹ '{}' trừ {}, còn lại: {}", nguonQuy.getTenNguonQuy(), soTien, nguonQuy.getConLai());

        // Cập nhật daChi của chương trình
        chuongTrinh.setDaChi((chuongTrinh.getDaChi() != null ? chuongTrinh.getDaChi() : 0.0) + soTien);
        chuongTrinhRepository.save(chuongTrinh);
    }
}