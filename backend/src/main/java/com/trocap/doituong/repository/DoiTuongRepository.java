package com.trocap.doituong.repository;

import com.trocap.doituong.model.DoiTuongHuongTruCap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DoiTuongRepository extends MongoRepository<DoiTuongHuongTruCap, String> {
    Optional<DoiTuongHuongTruCap> findByCode(String code);
    Page<DoiTuongHuongTruCap> findByCategory(String category, Pageable pageable);
    Page<DoiTuongHuongTruCap> findByStatus(String status, Pageable pageable);
    Page<DoiTuongHuongTruCap> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);
    long countByCategory(String category);
    long countByStatus(String status);
    long countByProgramId(String programId);
    boolean existsByIdNumber(String idNumber);
}
