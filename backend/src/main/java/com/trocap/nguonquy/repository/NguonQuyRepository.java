package com.trocap.nguonquy.repository;

import com.trocap.nguonquy.model.NguonQuy;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NguonQuyRepository extends MongoRepository<NguonQuy, String> {
    List<NguonQuy> findByTrangThai(String trangThai);
    List<NguonQuy> findByLoai(String loai);
}
