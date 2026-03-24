package com.trocap.tailieu.service;

import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.hoso.model.HoSoHoTro;
import com.trocap.hoso.repository.HoSoRepository;
import com.trocap.tailieu.dto.OcrResponse;
import com.trocap.tailieu.model.TaiLieuDinhKem;
import com.trocap.tailieu.repository.TaiLieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaiLieuService {

    private final TaiLieuRepository taiLieuRepository;
    private final HoSoRepository hoSoRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final FileStorageService fileStorageService;
    private final OcrService ocrService;

    // ─── Upload tài liệu ────────────────────────────────────────────
    public TaiLieuDinhKem upload(String applicationId, MultipartFile file, String username) {
        // Validate hồ sơ tồn tại
        HoSoHoTro hoSo = hoSoRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ: " + applicationId));

        // Kiểm tra quyền: chủ hồ sơ hoặc OFFICER/ADMIN
        verifyAccess(hoSo, username);

        // Lưu file lên disk
        String storedFilename = fileStorageService.store(file);

        // Lưu metadata vào MongoDB
        TaiLieuDinhKem taiLieu = TaiLieuDinhKem.builder()
                .hoSoHoTroId(applicationId)
                .tenTaiLieu(file.getOriginalFilename())
                .duongDanFile(storedFilename)
                .loaiFile(file.getContentType())
                .kichThuoc(file.getSize())
                .uploadedBy(username)
                .build();

        return taiLieuRepository.save(taiLieu);
    }

    // ─── Danh sách tài liệu theo hồ sơ ─────────────────────────────
    public List<TaiLieuDinhKem> findByHoSo(String applicationId, String username) {
        HoSoHoTro hoSo = hoSoRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ: " + applicationId));

        verifyAccess(hoSo, username);
        return taiLieuRepository.findByHoSoHoTroId(applicationId);
    }

    // ─── Chi tiết tài liệu ──────────────────────────────────────────
    public TaiLieuDinhKem findById(String id) {
        return taiLieuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu: " + id));
    }

    // ─── Chi tiết tài liệu (có kiểm tra quyền) ─────────────────────
    public TaiLieuDinhKem findByIdWithAccess(String id, String username) {
        TaiLieuDinhKem taiLieu = findById(id);
        HoSoHoTro hoSo = hoSoRepository.findById(taiLieu.getHoSoHoTroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy hồ sơ: " + taiLieu.getHoSoHoTroId()));
        verifyAccess(hoSo, username);
        return taiLieu;
    }
    // ─── Xóa tài liệu ──────────────────────────────────────────────
    public void delete(String id, String username) {
        TaiLieuDinhKem taiLieu = findById(id);

        // Validate hồ sơ + quyền
        HoSoHoTro hoSo = hoSoRepository.findById(taiLieu.getHoSoHoTroId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ: " + taiLieu.getHoSoHoTroId()));
        verifyAccess(hoSo, username);

        // Xóa file trên disk
        fileStorageService.delete(taiLieu.getDuongDanFile());

        // Xóa metadata
        taiLieuRepository.deleteById(id);
    }

    // ─── Xử lý OCR ─────────────────────────────────────────────────
    public OcrResponse processOcr(String attachmentId) {
        TaiLieuDinhKem taiLieu = findById(attachmentId);

        // Kiểm tra loại file có hỗ trợ OCR không
        if (!ocrService.supports(taiLieu.getLoaiFile())) {
            throw new BadRequestException(
                    "Loại file không hỗ trợ OCR: " + taiLieu.getLoaiFile()
                    + ". Chỉ hỗ trợ: PDF, PNG, JPG, JPEG");
        }

        // Lấy đường dẫn file trên disk
        Path filePath = fileStorageService.getFilePath(taiLieu.getDuongDanFile());

        // Gọi OCR engine
        String ocrResult = ocrService.extractText(filePath, taiLieu.getLoaiFile());

        // Lưu kết quả vào MongoDB
        taiLieu.setKetQuaOcr(ocrResult);
        taiLieuRepository.save(taiLieu);

        // Trả response
        return OcrResponse.builder()
                .attachmentId(taiLieu.getId())
                .tenTaiLieu(taiLieu.getTenTaiLieu())
                .loaiFile(taiLieu.getLoaiFile())
                .ketQuaOcr(ocrResult)
                .processedAt(LocalDateTime.now())
                .build();
    }

    // ─── Helper: Kiểm tra quyền truy cập ────────────────────────────
    private void verifyAccess(HoSoHoTro hoSo, String username) {
        NguoiDung user = nguoiDungRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));

        // ADMIN/OFFICER luôn có quyền
        if (user.getRoles() != null
                && (user.getRoles().contains("ADMIN") || user.getRoles().contains("OFFICER"))) {
            return;
        }

        // CITIZEN chỉ truy cập tài liệu hồ sơ của mình
        if (!user.getId().equals(hoSo.getNguoiDungId())) {
            throw new BadRequestException("Bạn không có quyền truy cập tài liệu của hồ sơ này");
        }
    }
}
