package com.trocap.doituong.repository;

import com.trocap.doituong.model.NhomDoiTuong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NhomDoiTuongRepository extends MongoRepository<NhomDoiTuong, String> {

    // Tìm kiếm theo tên (case-insensitive, partial match)
    Page<NhomDoiTuong> findByTenDoiTuongContainingIgnoreCase(String keyword, Pageable pageable);

    // Kiểm tra trùng tên
    boolean existsByTenDoiTuong(String tenDoiTuong);
}
