package com.trocap.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.trocap.auth.repository.NguoiDungRepository;

/**
 * DataSeeder — chỉ chạy khi DB hoàn toàn rỗng.
 * Dữ liệu thật được import qua: database/import-data.bat
 *
 * Nếu DB đã có dữ liệu (từ mongorestore), class này bỏ qua hoàn toàn.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public void run(String... args) {
        long userCount = nguoiDungRepository.count();
        if (userCount > 0) {
            log.info("DB đã có {} người dùng — bỏ qua DataSeeder. Dữ liệu thật đang được sử dụng.", userCount);
            return;
        }

        // DB rỗng: nhắc người dùng import dữ liệu thay vì seed tĩnh
        log.warn("=============================================================");
        log.warn("  CẢNH BÁO: Database đang rỗng!");
        log.warn("  Hãy import dữ liệu thật bằng lệnh:");
        log.warn("    cd database && import-data.bat   (Windows)");
        log.warn("    cd database && ./import-data.sh  (Linux/Mac)");
        log.warn("=============================================================");
    }
}
