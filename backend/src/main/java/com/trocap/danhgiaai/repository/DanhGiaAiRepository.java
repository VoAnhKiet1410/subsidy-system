package com.trocap.danhgiaai.repository;

import com.trocap.danhgiaai.model.DanhGiaAi;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DanhGiaAiRepository extends MongoRepository<DanhGiaAi, String> {
    List<DanhGiaAi> findByHoSoId(String hoSoId);
    Optional<DanhGiaAi> findFirstByHoSoIdOrderByCreatedAtDesc(String hoSoId);
}
