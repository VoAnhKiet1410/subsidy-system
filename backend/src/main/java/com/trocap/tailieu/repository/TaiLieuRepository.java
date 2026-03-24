package com.trocap.tailieu.repository;

import com.trocap.tailieu.model.TaiLieuDinhKem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaiLieuRepository extends MongoRepository<TaiLieuDinhKem, String> {

    // Lấy tất cả tài liệu theo hồ sơ
    List<TaiLieuDinhKem> findByHoSoHoTroId(String hoSoHoTroId);

    // Đếm tài liệu theo hồ sơ
    long countByHoSoHoTroId(String hoSoHoTroId);
}
