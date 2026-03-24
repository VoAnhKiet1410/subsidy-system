package com.trocap.chuongtrinh.repository;

import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ChuongTrinhRepository extends MongoRepository<ChuongTrinhTruCap, String> {

    // Lọc theo trạng thái (phân trang)
    Page<ChuongTrinhTruCap> findByTrangThai(String trangThai, Pageable pageable);

    // Lọc theo khoảng ngày: chương trình có ngày bắt đầu >= from VÀ ngày kết thúc <= to
    @Query("{ 'ngayBatDau': { $gte: ?0 }, 'ngayKetThuc': { $lte: ?1 } }")
    Page<ChuongTrinhTruCap> findByDateRange(LocalDate from, LocalDate to, Pageable pageable);

    // Tìm kiếm theo tên
    Page<ChuongTrinhTruCap> findByTenChuongTrinhContainingIgnoreCase(String keyword, Pageable pageable);

    // Danh sách chương trình theo nguồn quỹ
    List<ChuongTrinhTruCap> findByNguonQuyId(String nguonQuyId);

    // Đếm theo trạng thái (dùng cho dashboard)
    long countByTrangThai(String trangThai);
}
