package com.trocap.export;

import com.trocap.chitra.model.ChiTraTruCap;
import com.trocap.chuongtrinh.model.ChuongTrinhTruCap;
import com.trocap.doituong.model.DoiTuongHuongTruCap;
import com.trocap.hoso.model.HoSoHoTro;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service xuất báo cáo Excel chuyên nghiệp — styling đẹp mắt,
 * header rõ ràng, auto-fit cột, định dạng số/ngày tháng chuẩn.
 */
@Service
@Slf4j
public class ExcelExportService {

    // ─── Colors ────────────────────────────────────────────────────
    private static final byte[] PRIMARY_RGB  = {0, 80, (byte) 203};
    private static final byte[] HEADER_BG    = {0, 63, (byte) 164};
    private static final byte[] SUBHEADER_BG = {(byte) 232, (byte) 240, (byte) 254};
    private static final byte[] BORDER_COLOR = {(byte) 189, (byte) 195, (byte) 199};
    private static final byte[] STRIPE_BG    = {(byte) 245, (byte) 247, (byte) 250};
    private static final byte[] SUCCESS_BG   = {(byte) 209, (byte) 250, (byte) 229};
    private static final byte[] WARNING_BG   = {(byte) 254, (byte) 243, (byte) 199};
    private static final byte[] DANGER_BG    = {(byte) 254, (byte) 226, (byte) 226};

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ═══════════════════════════════════════════════════════════════
    // 1. XUẤT DANH SÁCH ĐỐI TƯỢNG HƯỞNG TRỢ CẤP
    // ═══════════════════════════════════════════════════════════════
    public byte[] exportBeneficiaries(List<DoiTuongHuongTruCap> list) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Đối tượng hưởng trợ cấp");
            var styles = createStyles(wb);

            // ── Title ──
            createTitle(sheet, styles, "BÁO CÁO DANH SÁCH ĐỐI TƯỢNG HƯỞNG TRỢ CẤP", 8);
            createSubtitle(sheet, styles, "Ngày xuất: " + LocalDate.now().format(DATE_FMT) + "  |  Tổng: " + list.size() + " đối tượng", 8);

            // ── Header ──
            String[] headers = {"STT", "Mã hồ sơ", "Họ và tên", "SĐT", "Địa chỉ", "CCCD", "Phân loại", "Trạng thái", "Mức trợ cấp"};
            createHeader(sheet, styles, 3, headers);

            // ── Data ──
            int rowIdx = 4;
            for (int i = 0; i < list.size(); i++) {
                DoiTuongHuongTruCap dt = list.get(i);
                XSSFRow row = sheet.createRow(rowIdx++);
                boolean odd = i % 2 == 1;

                cell(row, 0, i + 1, odd ? styles.stripeNum : styles.bodyNum);
                cell(row, 1, dt.getCode(), odd ? styles.stripeText : styles.bodyText);
                cell(row, 2, dt.getFullName(), odd ? styles.stripeBold : styles.bodyBold);
                cell(row, 3, dt.getPhone(), odd ? styles.stripeText : styles.bodyText);
                cell(row, 4, dt.getAddress(), odd ? styles.stripeText : styles.bodyText);
                cell(row, 5, dt.getIdNumber(), odd ? styles.stripeText : styles.bodyText);
                cell(row, 6, translateCategory(dt.getCategory()), odd ? styles.stripeText : styles.bodyText);
                cellStatus(row, 7, dt.getStatus(), styles);
                cell(row, 8, dt.getSubsidyAmount(), odd ? styles.stripeMoney : styles.bodyMoney);
            }

            // ── Footer tổng ──
            XSSFRow totalRow = sheet.createRow(rowIdx + 1);
            cell(totalRow, 7, "TỔNG CỘNG:", styles.totalLabel);
            double total = list.stream().mapToDouble(d -> d.getSubsidyAmount() != null ? d.getSubsidyAmount() : 0).sum();
            cell(totalRow, 8, total, styles.totalMoney);

            autoFitColumns(sheet, headers.length);
            return toBytes(wb);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // 2. XUẤT DANH SÁCH CHƯƠNG TRÌNH TRỢ CẤP
    // ═══════════════════════════════════════════════════════════════
    public byte[] exportPrograms(List<ChuongTrinhTruCap> list) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Chương trình trợ cấp");
            var styles = createStyles(wb);

            createTitle(sheet, styles, "BÁO CÁO CHƯƠNG TRÌNH TRỢ CẤP XÃ HỘI", 7);
            createSubtitle(sheet, styles, "Ngày xuất: " + LocalDate.now().format(DATE_FMT) + "  |  Tổng: " + list.size() + " chương trình", 7);

            String[] headers = {"STT", "Tên chương trình", "Ngân sách", "Đã chi", "Còn lại", "Trạng thái", "Ngày bắt đầu", "Ngày kết thúc"};
            createHeader(sheet, styles, 3, headers);

            int rowIdx = 4;
            for (int i = 0; i < list.size(); i++) {
                ChuongTrinhTruCap ct = list.get(i);
                XSSFRow row = sheet.createRow(rowIdx++);
                boolean odd = i % 2 == 1;

                double nganSach = ct.getNganSach() != null ? ct.getNganSach() : 0;
                double daChi = ct.getDaChi() != null ? ct.getDaChi() : 0;

                cell(row, 0, i + 1, odd ? styles.stripeNum : styles.bodyNum);
                cell(row, 1, ct.getTenChuongTrinh(), odd ? styles.stripeBold : styles.bodyBold);
                cell(row, 2, nganSach, odd ? styles.stripeMoney : styles.bodyMoney);
                cell(row, 3, daChi, odd ? styles.stripeMoney : styles.bodyMoney);
                cell(row, 4, nganSach - daChi, odd ? styles.stripeMoney : styles.bodyMoney);
                cellStatus(row, 5, ct.getTrangThai(), styles);
                cell(row, 6, formatDate(ct.getNgayBatDau()), odd ? styles.stripeText : styles.bodyText);
                cell(row, 7, formatDate(ct.getNgayKetThuc()), odd ? styles.stripeText : styles.bodyText);
            }

            // Tổng ngân sách
            XSSFRow totalRow = sheet.createRow(rowIdx + 1);
            cell(totalRow, 1, "TỔNG CỘNG:", styles.totalLabel);
            double tongNS = list.stream().mapToDouble(c -> c.getNganSach() != null ? c.getNganSach() : 0).sum();
            double tongChi = list.stream().mapToDouble(c -> c.getDaChi() != null ? c.getDaChi() : 0).sum();
            cell(totalRow, 2, tongNS, styles.totalMoney);
            cell(totalRow, 3, tongChi, styles.totalMoney);
            cell(totalRow, 4, tongNS - tongChi, styles.totalMoney);

            autoFitColumns(sheet, headers.length);
            return toBytes(wb);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // 3. XUẤT DANH SÁCH HỒ SƠ HỖ TRỢ
    // ═══════════════════════════════════════════════════════════════
    public byte[] exportApplications(List<HoSoHoTro> list) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Hồ sơ hỗ trợ");
            var styles = createStyles(wb);

            createTitle(sheet, styles, "BÁO CÁO HỒ SƠ ĐỀ NGHỊ HỖ TRỢ", 8);
            createSubtitle(sheet, styles, "Ngày xuất: " + LocalDate.now().format(DATE_FMT) + "  |  Tổng: " + list.size() + " hồ sơ", 8);

            String[] headers = {"STT", "Mã hồ sơ", "Mô tả", "Số tiền đề xuất", "Ngày nộp", "Trạng thái", "Người xét duyệt", "Lý do từ chối", "Ngày tạo"};
            createHeader(sheet, styles, 3, headers);

            int rowIdx = 4;
            for (int i = 0; i < list.size(); i++) {
                HoSoHoTro hs = list.get(i);
                XSSFRow row = sheet.createRow(rowIdx++);
                boolean odd = i % 2 == 1;

                cell(row, 0, i + 1, odd ? styles.stripeNum : styles.bodyNum);
                cell(row, 1, hs.getId(), odd ? styles.stripeText : styles.bodyText);
                cell(row, 2, hs.getMoTa(), odd ? styles.stripeText : styles.bodyText);
                cell(row, 3, hs.getSoTienDeXuat(), odd ? styles.stripeMoney : styles.bodyMoney);
                cell(row, 4, formatDate(hs.getNgayNopHoSo()), odd ? styles.stripeText : styles.bodyText);
                cellStatus(row, 5, hs.getTrangThai(), styles);
                cell(row, 6, hs.getReviewedBy(), odd ? styles.stripeText : styles.bodyText);
                cell(row, 7, hs.getLyDoTuChoi(), odd ? styles.stripeText : styles.bodyText);
                cell(row, 8, formatDateTime(hs.getCreatedAt()), odd ? styles.stripeText : styles.bodyText);
            }

            XSSFRow totalRow = sheet.createRow(rowIdx + 1);
            cell(totalRow, 2, "TỔNG CỘNG:", styles.totalLabel);
            double total = list.stream().mapToDouble(h -> h.getSoTienDeXuat() != null ? h.getSoTienDeXuat() : 0).sum();
            cell(totalRow, 3, total, styles.totalMoney);

            autoFitColumns(sheet, headers.length);
            return toBytes(wb);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // 4. XUẤT DANH SÁCH CHI TRẢ
    // ═══════════════════════════════════════════════════════════════
    public byte[] exportPayments(List<ChiTraTruCap> list) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Chi trả trợ cấp");
            var styles = createStyles(wb);

            createTitle(sheet, styles, "BÁO CÁO CHI TRẢ TRỢ CẤP", 7);
            createSubtitle(sheet, styles, "Ngày xuất: " + LocalDate.now().format(DATE_FMT) + "  |  Tổng: " + list.size() + " giao dịch", 7);

            String[] headers = {"STT", "Mã GD", "Mã hồ sơ", "Số tiền", "Phương thức", "Trạng thái", "Ngày chi trả", "Người xử lý"};
            createHeader(sheet, styles, 3, headers);

            int rowIdx = 4;
            for (int i = 0; i < list.size(); i++) {
                ChiTraTruCap ct = list.get(i);
                XSSFRow row = sheet.createRow(rowIdx++);
                boolean odd = i % 2 == 1;

                cell(row, 0, i + 1, odd ? styles.stripeNum : styles.bodyNum);
                cell(row, 1, ct.getId(), odd ? styles.stripeText : styles.bodyText);
                cell(row, 2, ct.getHoSoHoTroId(), odd ? styles.stripeText : styles.bodyText);
                cell(row, 3, ct.getSoTien(), odd ? styles.stripeMoney : styles.bodyMoney);
                cell(row, 4, translatePaymentMethod(ct.getPhuongThuc()), odd ? styles.stripeText : styles.bodyText);
                cellStatus(row, 5, ct.getTrangThai(), styles);
                cell(row, 6, formatDate(ct.getNgayChiTra()), odd ? styles.stripeText : styles.bodyText);
                cell(row, 7, ct.getProcessedBy(), odd ? styles.stripeText : styles.bodyText);
            }

            XSSFRow totalRow = sheet.createRow(rowIdx + 1);
            cell(totalRow, 2, "TỔNG CỘNG:", styles.totalLabel);
            double total = list.stream().mapToDouble(c -> c.getSoTien() != null ? c.getSoTien() : 0).sum();
            cell(totalRow, 3, total, styles.totalMoney);

            autoFitColumns(sheet, headers.length);
            return toBytes(wb);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // STYLES — Tạo style hệ thống
    // ═══════════════════════════════════════════════════════════════

    record Styles(
        CellStyle title, CellStyle subtitle, CellStyle header,
        CellStyle bodyText, CellStyle bodyBold, CellStyle bodyNum, CellStyle bodyMoney,
        CellStyle stripeText, CellStyle stripeBold, CellStyle stripeNum, CellStyle stripeMoney,
        CellStyle statusSuccess, CellStyle statusWarning, CellStyle statusDanger, CellStyle statusDefault,
        CellStyle totalLabel, CellStyle totalMoney
    ) {}

    private Styles createStyles(XSSFWorkbook wb) {
        // ── Fonts ──
        XSSFFont titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleFont.setColor(new XSSFColor(PRIMARY_RGB, null));

        XSSFFont subtitleFont = wb.createFont();
        subtitleFont.setFontName("Arial");
        subtitleFont.setFontHeightInPoints((short) 10);
        subtitleFont.setColor(new XSSFColor(new byte[]{100, 100, 100}, null)); // all < 128, OK

        XSSFFont headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        XSSFFont bodyFont = wb.createFont();
        bodyFont.setFontName("Arial");
        bodyFont.setFontHeightInPoints((short) 10);

        XSSFFont boldFont = wb.createFont();
        boldFont.setFontName("Arial");
        boldFont.setFontHeightInPoints((short) 10);
        boldFont.setBold(true);

        XSSFFont totalFont = wb.createFont();
        totalFont.setFontName("Arial");
        totalFont.setFontHeightInPoints((short) 11);
        totalFont.setBold(true);
        totalFont.setColor(new XSSFColor(PRIMARY_RGB, null));

        XSSFFont statusFont = wb.createFont();
        statusFont.setFontName("Arial");
        statusFont.setFontHeightInPoints((short) 9);
        statusFont.setBold(true);

        // ── Data format ──
        DataFormat fmt = wb.createDataFormat();
        short moneyFmt = fmt.getFormat("#,##0 \"₫\"");

        // ── Title style ──
        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.LEFT);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // ── Subtitle style ──
        CellStyle subtitleStyle = wb.createCellStyle();
        subtitleStyle.setFont(subtitleFont);
        subtitleStyle.setAlignment(HorizontalAlignment.LEFT);

        // ── Header style ──
        XSSFCellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(new XSSFColor(HEADER_BG, null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setWrapText(true);
        setBorder(headerStyle, BorderStyle.THIN, new XSSFColor(HEADER_BG, null));

        // ── Body styles ──
        CellStyle bodyText = createBodyStyle(wb, bodyFont, HorizontalAlignment.LEFT, null, (short) 0);
        CellStyle bodyBold = createBodyStyle(wb, boldFont, HorizontalAlignment.LEFT, null, (short) 0);
        CellStyle bodyNum  = createBodyStyle(wb, bodyFont, HorizontalAlignment.CENTER, null, (short) 0);
        CellStyle bodyMoney = createBodyStyle(wb, bodyFont, HorizontalAlignment.RIGHT, null, moneyFmt);

        // ── Stripe styles (odd rows) ──
        CellStyle stripeText = createBodyStyle(wb, bodyFont, HorizontalAlignment.LEFT, STRIPE_BG, (short) 0);
        CellStyle stripeBold = createBodyStyle(wb, boldFont, HorizontalAlignment.LEFT, STRIPE_BG, (short) 0);
        CellStyle stripeNum  = createBodyStyle(wb, bodyFont, HorizontalAlignment.CENTER, STRIPE_BG, (short) 0);
        CellStyle stripeMoney = createBodyStyle(wb, bodyFont, HorizontalAlignment.RIGHT, STRIPE_BG, moneyFmt);

        // ── Status styles ──
        CellStyle statusSuccess = createStatusStyle(wb, statusFont, SUCCESS_BG, new byte[]{5, 100, 50});
        CellStyle statusWarning = createStatusStyle(wb, statusFont, WARNING_BG, new byte[]{120, 90, 0});
        CellStyle statusDanger  = createStatusStyle(wb, statusFont, DANGER_BG, new byte[]{(byte) 180, 20, 20});
        CellStyle statusDefault = createStatusStyle(wb, statusFont, SUBHEADER_BG, new byte[]{50, 60, 80});

        // ── Total styles ──
        XSSFCellStyle totalLabel = wb.createCellStyle();
        totalLabel.setFont(totalFont);
        totalLabel.setAlignment(HorizontalAlignment.RIGHT);
        totalLabel.setVerticalAlignment(VerticalAlignment.CENTER);
        totalLabel.setBorderTop(BorderStyle.DOUBLE);
        totalLabel.setTopBorderColor(new XSSFColor(PRIMARY_RGB, null));

        XSSFCellStyle totalMoney = wb.createCellStyle();
        totalMoney.setFont(totalFont);
        totalMoney.setAlignment(HorizontalAlignment.RIGHT);
        totalMoney.setDataFormat(moneyFmt);
        totalMoney.setBorderTop(BorderStyle.DOUBLE);
        totalMoney.setTopBorderColor(new XSSFColor(PRIMARY_RGB, null));

        return new Styles(titleStyle, subtitleStyle, headerStyle,
            bodyText, bodyBold, bodyNum, bodyMoney,
            stripeText, stripeBold, stripeNum, stripeMoney,
            statusSuccess, statusWarning, statusDanger, statusDefault,
            totalLabel, totalMoney);
    }

    private XSSFCellStyle createBodyStyle(XSSFWorkbook wb, Font font, HorizontalAlignment align, byte[] bgColor, short dataFmt) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(align);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (bgColor != null) {
            style.setFillForegroundColor(new XSSFColor(bgColor, null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (dataFmt > 0) {
            style.setDataFormat(dataFmt);
        }
        setBorder(style, BorderStyle.THIN, new XSSFColor(BORDER_COLOR, null));
        return style;
    }

    private XSSFCellStyle createStatusStyle(XSSFWorkbook wb, Font font, byte[] bgColor, byte[] fontColor) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setFontName("Arial");
        f.setFontHeightInPoints((short) 9);
        f.setBold(true);
        f.setColor(new XSSFColor(fontColor, null));
        style.setFont(f);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(new XSSFColor(bgColor, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(style, BorderStyle.THIN, new XSSFColor(BORDER_COLOR, null));
        return style;
    }

    private void setBorder(XSSFCellStyle style, BorderStyle border, XSSFColor color) {
        style.setBorderBottom(border); style.setBottomBorderColor(color);
        style.setBorderTop(border);    style.setTopBorderColor(color);
        style.setBorderLeft(border);   style.setLeftBorderColor(color);
        style.setBorderRight(border);  style.setRightBorderColor(color);
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════

    private void createTitle(XSSFSheet sheet, Styles styles, String title, int colSpan) {
        XSSFRow row = sheet.createRow(0);
        row.setHeightInPoints(32);
        XSSFCell c = row.createCell(0);
        c.setCellValue(title);
        c.setCellStyle(styles.title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colSpan));
    }

    private void createSubtitle(XSSFSheet sheet, Styles styles, String text, int colSpan) {
        XSSFRow row = sheet.createRow(1);
        row.setHeightInPoints(20);
        XSSFCell c = row.createCell(0);
        c.setCellValue(text);
        c.setCellStyle(styles.subtitle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, colSpan));
    }

    private void createHeader(XSSFSheet sheet, Styles styles, int rowIdx, String[] headers) {
        XSSFRow row = sheet.createRow(rowIdx);
        row.setHeightInPoints(28);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell c = row.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(styles.header);
        }
        sheet.setAutoFilter(new CellRangeAddress(rowIdx, rowIdx, 0, headers.length - 1));
    }

    private void cell(XSSFRow row, int col, String value, CellStyle style) {
        XSSFCell c = row.createCell(col);
        c.setCellValue(value != null ? value : "");
        c.setCellStyle(style);
    }

    private void cell(XSSFRow row, int col, double value, CellStyle style) {
        XSSFCell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private void cell(XSSFRow row, int col, int value, CellStyle style) {
        XSSFCell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private void cell(XSSFRow row, int col, Double value, CellStyle style) {
        XSSFCell c = row.createCell(col);
        c.setCellValue(value != null ? value : 0);
        c.setCellStyle(style);
    }

    private void cellStatus(XSSFRow row, int col, String status, Styles styles) {
        XSSFCell c = row.createCell(col);
        c.setCellValue(translateStatus(status));
        c.setCellStyle(getStatusStyle(status, styles));
    }

    private CellStyle getStatusStyle(String status, Styles styles) {
        if (status == null) return styles.statusDefault;
        return switch (status.toUpperCase()) {
            case "ACTIVE", "APPROVED", "SUCCESS", "OPEN", "PAID" -> styles.statusSuccess;
            case "PENDING", "SUBMITTED", "UNDER_REVIEW", "UPCOMING" -> styles.statusWarning;
            case "REJECTED", "FAILED", "CANCELLED", "CLOSED", "PAUSED" -> styles.statusDanger;
            default -> styles.statusDefault;
        };
    }

    private String translateStatus(String status) {
        if (status == null) return "";
        return switch (status.toUpperCase()) {
            case "ACTIVE" -> "Đang hoạt động";
            case "PENDING" -> "Chờ xử lý";
            case "APPROVED" -> "Đã duyệt";
            case "REJECTED" -> "Từ chối";
            case "PAUSED" -> "Tạm dừng";
            case "OPEN" -> "Đang mở";
            case "CLOSED" -> "Đã đóng";
            case "UPCOMING" -> "Sắp mở";
            case "DRAFT" -> "Bản nháp";
            case "SUBMITTED" -> "Đã nộp";
            case "UNDER_REVIEW" -> "Đang xét duyệt";
            case "PAID" -> "Đã chi trả";
            case "SUCCESS" -> "Thành công";
            case "FAILED" -> "Thất bại";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }

    private String translateCategory(String cat) {
        if (cat == null) return "";
        return switch (cat.toUpperCase()) {
            case "THU_NHAP_THAP" -> "Thu nhập thấp";
            case "KHUYET_TAT" -> "Khuyết tật";
            case "NGUOI_GIA" -> "Người già";
            case "TRE_EM" -> "Trẻ em";
            case "DON_THAN" -> "Đơn thân";
            case "HIV" -> "HIV/AIDS";
            case "THIEN_TAI" -> "Thiên tai";
            default -> cat;
        };
    }

    private String translatePaymentMethod(String method) {
        if (method == null) return "";
        return switch (method.toUpperCase()) {
            case "BANK_TRANSFER" -> "Chuyển khoản";
            case "CASH" -> "Tiền mặt";
            case "MOMO" -> "MoMo";
            case "VNPAY" -> "VNPay";
            default -> method;
        };
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FMT) : "";
    }

    private String formatDateTime(LocalDateTime dt) {
        return dt != null ? dt.format(DATETIME_FMT) : "";
    }

    private void autoFitColumns(XSSFSheet sheet, int colCount) {
        for (int i = 0; i < colCount; i++) {
            sheet.autoSizeColumn(i);
            // Thêm padding (min 12, max 50)
            int width = Math.max(sheet.getColumnWidth(i) + 1200, 3200);
            sheet.setColumnWidth(i, Math.min(width, 12800));
        }
        // Freeze header row
        sheet.createFreezePane(0, 4);
    }

    private byte[] toBytes(XSSFWorkbook wb) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }
}
