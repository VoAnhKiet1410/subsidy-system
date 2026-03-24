package com.trocap.thongbao.repository;

import com.trocap.thongbao.model.ThongBao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThongBaoRepository extends MongoRepository<ThongBao, String> {

    // Phân trang thông báo theo người dùng
    Page<ThongBao> findByNguoiDungId(String nguoiDungId, Pageable pageable);

    // Lọc theo daDoc
    Page<ThongBao> findByNguoiDungIdAndDaDoc(String nguoiDungId, boolean daDoc, Pageable pageable);

    // Đếm chưa đọc
    long countByNguoiDungIdAndDaDoc(String nguoiDungId, boolean daDoc);

    // List (không phân trang) — dùng cho markAllRead
    java.util.List<ThongBao> findByNguoiDungIdAndDaDoc(String nguoiDungId, boolean daDoc);
}
