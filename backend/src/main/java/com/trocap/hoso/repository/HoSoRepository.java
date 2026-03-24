package com.trocap.hoso.repository;

import com.trocap.hoso.model.HoSoHoTro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;

public interface HoSoRepository extends MongoRepository<HoSoHoTro, String>, HoSoRepositoryCustom {

    // Lọc theo người nộp
    Page<HoSoHoTro> findByNguoiDungId(String nguoiDungId, Pageable pageable);

    // Lọc theo trạng thái
    Page<HoSoHoTro> findByTrangThai(String trangThai, Pageable pageable);

    // Lọc theo chương trình
    Page<HoSoHoTro> findByChuongTrinhId(String chuongTrinhId, Pageable pageable);

    // Lọc theo ngày nộp hồ sơ (khoảng)
    @Query("{ 'ngayNopHoSo': { $gte: ?0, $lte: ?1 } }")
    Page<HoSoHoTro> findByNgayNopRange(LocalDate from, LocalDate to, Pageable pageable);

    // CITIZEN xem hồ sơ theo user + trạng thái
    Page<HoSoHoTro> findByNguoiDungIdAndTrangThai(String nguoiDungId, String trangThai, Pageable pageable);

    // Đếm
    long countByTrangThai(String trangThai);
    long countByNguoiDungId(String nguoiDungId);
}
