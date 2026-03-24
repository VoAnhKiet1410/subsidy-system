package com.trocap.hoso.repository;

import com.trocap.hoso.model.HoSoHoTro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Custom repository cho dynamic query với nhiều filter đồng thời.
 */
public interface HoSoRepositoryCustom {

    /**
     * Tìm kiếm hồ sơ với nhiều điều kiện lọc.
     * Tất cả params đều optional — chỉ áp dụng khi != null.
     */
    Page<HoSoHoTro> search(
            String nguoiDungId,
            String chuongTrinhId,
            String doiTuongId,
            String trangThai,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );
}
