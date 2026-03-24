package com.trocap.nguoidung.repository;

import com.trocap.nguoidung.model.VaiTro;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VaiTroRepository extends MongoRepository<VaiTro, String> {
    Optional<VaiTro> findByMaVaiTro(String maVaiTro);
    boolean existsByMaVaiTro(String maVaiTro);
}
