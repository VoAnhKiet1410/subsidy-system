package com.trocap.doituong.service;

import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.doituong.model.DoiTuongHuongTruCap;
import com.trocap.doituong.repository.DoiTuongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DoiTuongService {

    private final DoiTuongRepository doiTuongRepository;

    public Page<DoiTuongHuongTruCap> findAll(Pageable pageable) {
        return doiTuongRepository.findAll(pageable);
    }

    public DoiTuongHuongTruCap findById(String id) {
        return doiTuongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đối tượng: " + id));
    }

    public Page<DoiTuongHuongTruCap> findByCategory(String category, Pageable pageable) {
        return doiTuongRepository.findByCategory(category, pageable);
    }

    public Page<DoiTuongHuongTruCap> findByStatus(String status, Pageable pageable) {
        return doiTuongRepository.findByStatus(status, pageable);
    }

    public Page<DoiTuongHuongTruCap> search(String keyword, Pageable pageable) {
        return doiTuongRepository.findByFullNameContainingIgnoreCase(keyword, pageable);
    }

    public DoiTuongHuongTruCap create(DoiTuongHuongTruCap doiTuong) {
        if (doiTuong.getIdNumber() != null && !doiTuong.getIdNumber().isBlank()
                && doiTuongRepository.existsByIdNumber(doiTuong.getIdNumber())) {
            throw new BadRequestException("Số CCCD " + doiTuong.getIdNumber() + " đã tồn tại trong hệ thống");
        }
        doiTuong.setCode("#HS-" + ThreadLocalRandom.current().nextInt(10000, 99999));
        if (doiTuong.getStatus() == null) {
            doiTuong.setStatus("PENDING");
        }
        return doiTuongRepository.save(doiTuong);
    }

    public DoiTuongHuongTruCap update(String id, DoiTuongHuongTruCap updated) {
        DoiTuongHuongTruCap existing = findById(id);
        if (updated.getFullName() != null) existing.setFullName(updated.getFullName());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        if (updated.getSubsidyAmount() != null) existing.setSubsidyAmount(updated.getSubsidyAmount());
        if (updated.getAiTrustScore() != null) existing.setAiTrustScore(updated.getAiTrustScore());
        return doiTuongRepository.save(existing);
    }

    public void delete(String id) {
        findById(id);
        doiTuongRepository.deleteById(id);
    }

    public long countByCategory(String category) {
        return doiTuongRepository.countByCategory(category);
    }

    public long countByStatus(String status) {
        return doiTuongRepository.countByStatus(status);
    }
}
