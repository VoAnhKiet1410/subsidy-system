package com.trocap.hoso.repository;

import com.trocap.hoso.model.HoSoHoTro;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Criteria API — xây query động dựa trên các filter được truyền vào.
 * Tất cả filter đều optional, chỉ áp dụng khi != null.
 */
@Repository
@RequiredArgsConstructor
public class HoSoRepositoryCustomImpl implements HoSoRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<HoSoHoTro> search(
            String nguoiDungId,
            String chuongTrinhId,
            String doiTuongId,
            String trangThai,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {

        List<Criteria> criteriaList = new ArrayList<>();

        // Lọc theo người nộp
        if (nguoiDungId != null && !nguoiDungId.isBlank()) {
            criteriaList.add(Criteria.where("nguoiDungId").is(nguoiDungId));
        }

        // Lọc theo chương trình
        if (chuongTrinhId != null && !chuongTrinhId.isBlank()) {
            criteriaList.add(Criteria.where("chuongTrinhId").is(chuongTrinhId));
        }

        // Lọc theo đối tượng hưởng trợ cấp
        if (doiTuongId != null && !doiTuongId.isBlank()) {
            criteriaList.add(Criteria.where("doiTuongId").is(doiTuongId));
        }

        // Lọc theo trạng thái
        if (trangThai != null && !trangThai.isBlank()) {
            criteriaList.add(Criteria.where("trangThai").is(trangThai));
        }

        // Lọc theo khoảng ngày nộp hồ sơ
        if (fromDate != null) {
            criteriaList.add(Criteria.where("ngayNopHoSo").gte(fromDate));
        }
        if (toDate != null) {
            criteriaList.add(Criteria.where("ngayNopHoSo").lte(toDate));
        }

        // Build query
        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // Đếm tổng số bản ghi (cho phân trang)
        long total = mongoTemplate.count(query, HoSoHoTro.class);

        // Áp dụng phân trang + sort
        query.with(pageable);

        List<HoSoHoTro> results = mongoTemplate.find(query, HoSoHoTro.class);

        return new PageImpl<>(results, pageable, total);
    }
}
