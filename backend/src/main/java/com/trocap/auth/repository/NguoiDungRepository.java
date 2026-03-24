package com.trocap.auth.repository;

import com.trocap.auth.model.NguoiDung;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NguoiDungRepository extends MongoRepository<NguoiDung, String> {
    Optional<NguoiDung> findByUsername(String username);
    Optional<NguoiDung> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
