package com.trocap.config;

import com.mongodb.MongoException; // ✅ FIX IMPORT
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class MongoIndexFixer implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        fixMaHoSoIndex();
        fixBeneficiariesCodeIndex();
    }

    private void fixBeneficiariesCodeIndex() {
        try {
            var indexOps = mongoTemplate.indexOps("beneficiaries");

            for (IndexInfo idx : indexOps.getIndexInfo()) {
                String name = idx.getName();

                if (name != null && (name.equals("code") || name.startsWith("code_")) && !idx.isSparse()) {
                    log.info("Dropping non-sparse index '{}' on beneficiaries", name);
                    indexOps.dropIndex(name);
                }
            }

        } catch (MongoException e) {
            log.error("MongoDB error while fixing beneficiaries code index: {}", e.getMessage(), e);
        } catch (DataAccessException e) {
            log.error("Data access error while fixing beneficiaries code index: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while fixing beneficiaries code index: {}", e.getMessage(), e);
        }
    }

    private void fixMaHoSoIndex() {
        try {
            var indexOps = mongoTemplate.indexOps("applications");

            // Drop index cũ
            for (IndexInfo idx : indexOps.getIndexInfo()) {
                String name = idx.getName();

                if (name != null && (name.startsWith("maHoSo") || name.equals("code") || name.startsWith("code_"))) {
                    log.info("Dropping old index '{}' on applications", name);
                    indexOps.dropIndex(name);
                }
            }

            // Tạo index mới
            indexOps.ensureIndex(
                    new Index()
                            .on("maHoSo", Sort.Direction.ASC)
                            .unique()
                            .sparse()
                            .named("maHoSo_sparse_unique")
            );

            log.info("Created sparse unique index 'maHoSo_sparse_unique' on applications");

        } catch (MongoException e) {
            log.error("MongoDB error while fixing maHoSo index: {}", e.getMessage(), e);
        } catch (DataAccessException e) {
            log.error("Data access error while fixing maHoSo index: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.warn("Không thể sửa index maHoSo: {}", e.getMessage());
        }
    }
}