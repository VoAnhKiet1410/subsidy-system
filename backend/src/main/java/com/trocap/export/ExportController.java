package com.trocap.export;

import com.trocap.chitra.repository.ChiTraRepository;
import com.trocap.chuongtrinh.repository.ChuongTrinhRepository;
import com.trocap.doituong.repository.DoiTuongRepository;
import com.trocap.hoso.repository.HoSoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * API xuất báo cáo Excel — chỉ ADMIN và OFFICER được phép.
 */
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'ACCOUNTANT')")
public class ExportController {

    private final ExcelExportService excelService;
    private final DoiTuongRepository doiTuongRepository;
    private final ChuongTrinhRepository chuongTrinhRepository;
    private final HoSoRepository hoSoRepository;
    private final ChiTraRepository chiTraRepository;

    private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /**
     * GET /api/export/beneficiaries — Xuất danh sách đối tượng
     */
    @GetMapping("/beneficiaries")
    public ResponseEntity<byte[]> exportBeneficiaries() throws IOException {
        log.info("Xuất báo cáo đối tượng hưởng trợ cấp");
        byte[] data = excelService.exportBeneficiaries(doiTuongRepository.findAll());
        return buildResponse(data, "Doi_Tuong_Huong_Tro_Cap");
    }

    /**
     * GET /api/export/programs — Xuất danh sách chương trình
     */
    @GetMapping("/programs")
    public ResponseEntity<byte[]> exportPrograms() throws IOException {
        log.info("Xuất báo cáo chương trình trợ cấp");
        byte[] data = excelService.exportPrograms(chuongTrinhRepository.findAll());
        return buildResponse(data, "Chuong_Trinh_Tro_Cap");
    }

    /**
     * GET /api/export/applications — Xuất danh sách hồ sơ
     */
    @GetMapping("/applications")
    public ResponseEntity<byte[]> exportApplications() throws IOException {
        log.info("Xuất báo cáo hồ sơ hỗ trợ");
        byte[] data = excelService.exportApplications(hoSoRepository.findAll());
        return buildResponse(data, "Ho_So_Ho_Tro");
    }

    /**
     * GET /api/export/payments — Xuất danh sách chi trả
     */
    @GetMapping("/payments")
    public ResponseEntity<byte[]> exportPayments() throws IOException {
        log.info("Xuất báo cáo chi trả trợ cấp");
        byte[] data = excelService.exportPayments(chiTraRepository.findAll());
        return buildResponse(data, "Chi_Tra_Tro_Cap");
    }

    private ResponseEntity<byte[]> buildResponse(byte[] data, String prefix) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String filename = prefix + "_" + date + ".xlsx";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(XLSX_CONTENT_TYPE))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + encodedFilename)
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(data);
    }
}
