package com.trocap.chuongtrinh.service;

import com.trocap.chuongtrinh.dto.ChuongTrinhRequest;
import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import com.trocap.chuongtrinh.repository.ChuongTrinhRepository;
import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.nguonquy.repository.NguonQuyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ChuongTrinhService {

    private final ChuongTrinhRepository chuongTrinhRepository;
    private final NguonQuyRepository nguonQuyRepository;

    // ─── Danh sách (phân trang) ─────────────────────────────────────
    public Page<ChuongTrinhTruCap> findAll(Pageable pageable) {
        return chuongTrinhRepository.findAll(pageable);
    }

    // ─── Chi tiết ───────────────────────────────────────────────────
    public ChuongTrinhTruCap findById(String id) {
        return chuongTrinhRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương trình: " + id));
    }

    // ─── Lọc theo trạng thái ────────────────────────────────────────
    public Page<ChuongTrinhTruCap> findByTrangThai(String trangThai, Pageable pageable) {
        return chuongTrinhRepository.findByTrangThai(trangThai, pageable);
    }

    // ─── Lọc theo khoảng ngày ───────────────────────────────────────
    public Page<ChuongTrinhTruCap> findByDateRange(LocalDate from, LocalDate to, Pageable pageable) {
        return chuongTrinhRepository.findByDateRange(from, to, pageable);
    }

    // ─── Tìm kiếm theo tên ─────────────────────────────────────────
    public Page<ChuongTrinhTruCap> search(String keyword, Pageable pageable) {
        return chuongTrinhRepository.findByTenChuongTrinhContainingIgnoreCase(keyword, pageable);
    }

    // ─── Tạo mới ────────────────────────────────────────────────────
    public ChuongTrinhTruCap create(ChuongTrinhRequest request, String createdBy) {
        // Validate: nguồn quỹ phải tồn tại
        if (!nguonQuyRepository.existsById(request.getNguonQuyId())) {
            throw new BadRequestException("Nguồn quỹ không tồn tại: " + request.getNguonQuyId());
        }
        // Validate: ngày bắt đầu <= ngày kết thúc
        if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
            throw new BadRequestException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc");
        }

        // Mặc định trạng thái UPCOMING nếu không truyền
        String trangThai = request.getTrangThai();
        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "UPCOMING";
        }

        ChuongTrinhTruCap ct = ChuongTrinhTruCap.builder()
                .tenChuongTrinh(request.getTenChuongTrinh())
                .moTa(request.getMoTa())
                .nguonQuyId(request.getNguonQuyId())
                .nganSach(request.getNganSach() != null ? request.getNganSach() : 0.0)
                .daChi(0.0)
                .ngayBatDau(request.getNgayBatDau())
                .ngayKetThuc(request.getNgayKetThuc())
                .trangThai(trangThai)
                .createdBy(createdBy)
                .build();

        return chuongTrinhRepository.save(ct);
    }

    // ─── Cập nhật ───────────────────────────────────────────────────
    public ChuongTrinhTruCap update(String id, ChuongTrinhRequest request) {
        ChuongTrinhTruCap ct = findById(id);

        // Validate: nguồn quỹ phải tồn tại nếu thay đổi
        if (request.getNguonQuyId() != null && !request.getNguonQuyId().equals(ct.getNguonQuyId())) {
            if (!nguonQuyRepository.existsById(request.getNguonQuyId())) {
                throw new BadRequestException("Nguồn quỹ không tồn tại: " + request.getNguonQuyId());
            }
            ct.setNguonQuyId(request.getNguonQuyId());
        }

        // Validate: ngày
        LocalDate start = request.getNgayBatDau() != null ? request.getNgayBatDau() : ct.getNgayBatDau();
        LocalDate end = request.getNgayKetThuc() != null ? request.getNgayKetThuc() : ct.getNgayKetThuc();
        if (start != null && end != null && start.isAfter(end)) {
            throw new BadRequestException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc");
        }

        if (request.getTenChuongTrinh() != null) ct.setTenChuongTrinh(request.getTenChuongTrinh());
        if (request.getMoTa() != null) ct.setMoTa(request.getMoTa());
        if (request.getNganSach() != null) ct.setNganSach(request.getNganSach());
        if (request.getNgayBatDau() != null) ct.setNgayBatDau(request.getNgayBatDau());
        if (request.getNgayKetThuc() != null) ct.setNgayKetThuc(request.getNgayKetThuc());
        if (request.getTrangThai() != null) ct.setTrangThai(request.getTrangThai());

        return chuongTrinhRepository.save(ct);
    }

    // ─── Xóa ────────────────────────────────────────────────────────
    public void delete(String id) {
        if (!chuongTrinhRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy chương trình: " + id);
        }
        chuongTrinhRepository.deleteById(id);
    }
}
