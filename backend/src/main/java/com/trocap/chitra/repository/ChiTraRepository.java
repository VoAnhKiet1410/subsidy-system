package com.trocap.chitra.repository;

import com.trocap.chitra.model.ChiTraTruCap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChiTraRepository extends MongoRepository<ChiTraTruCap, String> {

    // Lọc theo trạng thái
    Page<ChiTraTruCap> findByTrangThai(String trangThai, Pageable pageable);

    // Tìm theo hồ sơ
    List<ChiTraTruCap> findByHoSoHoTroId(String hoSoHoTroId);

    // Đếm
    long countByTrangThai(String trangThai);
}
