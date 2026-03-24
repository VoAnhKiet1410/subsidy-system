package com.trocap.thongbao.service;

import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.thongbao.dto.ThongBaoRequest;
import com.trocap.thongbao.model.ThongBao;
import com.trocap.thongbao.repository.ThongBaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;

    // ─── Danh sách thông báo (phân trang, lọc daDoc) ────────────────
    public Page<ThongBao> findByNguoiDungId(String nguoiDungId, Boolean daDoc, Pageable pageable) {
        if (daDoc != null) {
            return thongBaoRepository.findByNguoiDungIdAndDaDoc(nguoiDungId, daDoc, pageable);
        }
        return thongBaoRepository.findByNguoiDungId(nguoiDungId, pageable);
    }

    // ─── Chi tiết ───────────────────────────────────────────────────
    public ThongBao findById(String id) {
        return thongBaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo: " + id));
    }

    // ─── Đếm chưa đọc ──────────────────────────────────────────────
    public long countUnread(String nguoiDungId) {
        return thongBaoRepository.countByNguoiDungIdAndDaDoc(nguoiDungId, false);
    }

    // ─── Đánh dấu đã đọc ───────────────────────────────────────────
    public ThongBao markRead(String id) {
        ThongBao tb = findById(id);
        tb.setDaDoc(true);
        return thongBaoRepository.save(tb);
    }

    // ─── Đánh dấu tất cả đã đọc ────────────────────────────────────
    public void markAllRead(String nguoiDungId) {
        var unread = thongBaoRepository.findByNguoiDungIdAndDaDoc(nguoiDungId, false);
        unread.forEach(tb -> {
            tb.setDaDoc(true);
            thongBaoRepository.save(tb);
        });
    }

    // ─── ADMIN tạo thông báo thủ công ───────────────────────────────
    public ThongBao create(ThongBaoRequest request) {
        ThongBao tb = ThongBao.builder()
                .nguoiDungId(request.getNguoiDungId())
                .tieuDe(request.getTieuDe())
                .noiDung(request.getNoiDung())
                .loai(request.getLoai() != null ? request.getLoai() : "INFO")
                .link(request.getLink())
                .daDoc(false)
                .build();
        return thongBaoRepository.save(tb);
    }

    // ─── Xóa thông báo ─────────────────────────────────────────────
    public void delete(String id) {
        ThongBao tb = findById(id);
        thongBaoRepository.delete(tb);
    }

    // ═══════════════════════════════════════════════════════════════
    // HỆ THỐNG GỌI — dùng bởi HoSoService, ChiTraService, v.v.
    // ═══════════════════════════════════════════════════════════════

    /**
     * Tạo thông báo tự động từ hệ thống.
     * Được gọi từ các module khác (hồ sơ duyệt/từ chối, chi trả thành công...).
     */
    public void notify(String nguoiDungId, String loai, String tieuDe,
                       String noiDung, String entityType, String entityId, String link) {
        ThongBao tb = ThongBao.builder()
                .nguoiDungId(nguoiDungId)
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .loai(loai)
                .entityType(entityType)
                .entityId(entityId)
                .link(link)
                .daDoc(false)
                .build();
        thongBaoRepository.save(tb);
        log.info("Thông báo [{}] gửi tới user {}: {}", loai, nguoiDungId, tieuDe);
    }
}
