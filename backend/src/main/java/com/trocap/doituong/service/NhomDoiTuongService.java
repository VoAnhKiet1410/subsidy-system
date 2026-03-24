package com.trocap.doituong.service;

import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.doituong.dto.NhomDoiTuongRequest;
import com.trocap.doituong.model.NhomDoiTuong;
import com.trocap.doituong.repository.NhomDoiTuongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NhomDoiTuongService {

    private final NhomDoiTuongRepository nhomDoiTuongRepository;

    // Danh sách có phân trang
    public Page<NhomDoiTuong> findAll(Pageable pageable) {
        return nhomDoiTuongRepository.findAll(pageable);
    }

    // Chi tiết
    public NhomDoiTuong findById(String id) {
        return nhomDoiTuongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm đối tượng: " + id));
    }

    // Tìm kiếm theo tên
    public Page<NhomDoiTuong> search(String keyword, Pageable pageable) {
        return nhomDoiTuongRepository.findByTenDoiTuongContainingIgnoreCase(keyword, pageable);
    }

    // Tạo mới
    public NhomDoiTuong create(NhomDoiTuongRequest request) {
        // Kiểm tra trùng tên
        if (nhomDoiTuongRepository.existsByTenDoiTuong(request.getTenDoiTuong())) {
            throw new BadRequestException("Tên đối tượng đã tồn tại: " + request.getTenDoiTuong());
        }
        NhomDoiTuong nhom = NhomDoiTuong.builder()
                .tenDoiTuong(request.getTenDoiTuong())
                .moTa(request.getMoTa())
                .build();
        return nhomDoiTuongRepository.save(nhom);
    }

    // Cập nhật
    public NhomDoiTuong update(String id, NhomDoiTuongRequest request) {
        NhomDoiTuong nhom = findById(id);
        // Nếu đổi tên → kiểm tra trùng với nhóm khác
        if (!nhom.getTenDoiTuong().equals(request.getTenDoiTuong())
                && nhomDoiTuongRepository.existsByTenDoiTuong(request.getTenDoiTuong())) {
            throw new BadRequestException("Tên đối tượng đã tồn tại: " + request.getTenDoiTuong());
        }
        nhom.setTenDoiTuong(request.getTenDoiTuong());
        nhom.setMoTa(request.getMoTa());
        return nhomDoiTuongRepository.save(nhom);
    }

    // Xóa
    public void delete(String id) {
        if (!nhomDoiTuongRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy nhóm đối tượng: " + id);
        }
        nhomDoiTuongRepository.deleteById(id);
    }
}
