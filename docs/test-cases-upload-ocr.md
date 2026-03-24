# Test Cases Chuyên Sâu — Upload Tài Liệu & OCR

> Tổng số: **25 test cases** | Ngày tạo: 2026-03-24
> Lưu ý: Không bao gồm test AI review theo yêu cầu

---

## A. Upload Tài Liệu Đính Kèm

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| UPL-01 | Upload PDF | Hồ sơ tồn tại, CITIZEN là chủ hồ sơ | File: cmnd.pdf (500KB) | 1. POST /api/applications/{appId}/attachments multipart file=cmnd.pdf 2. Kiểm tra response | success=true, tenTaiLieu="cmnd.pdf", loaiFile="application/pdf", hoSoHoTroId=appId, duongDanFile chứa UUID | Cao | Positive |
| UPL-02 | Upload PNG | Hồ sơ tồn tại, CITIZEN là chủ hồ sơ | File: giayxacnhan.png (1MB) | 1. POST upload giayxacnhan.png | success=true, loaiFile="image/png" | Trung bình | Positive |
| UPL-03 | Upload JPG | Hồ sơ tồn tại | File: photo.jpg (800KB) | 1. POST upload photo.jpg | success=true, loaiFile="image/jpg" | Trung bình | Positive |
| UPL-04 | Upload JPEG | Hồ sơ tồn tại | File: scan.jpeg (600KB) | 1. POST upload scan.jpeg | success=true, loaiFile="image/jpeg" | Trung bình | Positive |
| UPL-05 | Upload .exe bị từ chối | Hồ sơ tồn tại | File: virus.exe | 1. POST upload virus.exe | 400 "Định dạng file không được hỗ trợ. Chỉ chấp nhận: PDF, PNG, JPG, JPEG" | Cao | Negative |
| UPL-06 | Upload .docx bị từ chối | Hồ sơ tồn tại | File: document.docx | 1. POST upload document.docx | 400 "Định dạng file không được hỗ trợ" | Trung bình | Negative |
| UPL-07 | Upload .zip bị từ chối | Hồ sơ tồn tại | File: archive.zip | 1. POST upload archive.zip | 400 "Định dạng file không được hỗ trợ" | Trung bình | Negative |
| UPL-08 | Upload file rỗng | Hồ sơ tồn tại | File rỗng (0 bytes) | 1. POST upload file rỗng | 400 "File upload không được rỗng" | Trung bình | Boundary |
| UPL-09 | Upload file > 10MB | Hồ sơ tồn tại, giới hạn server 10MB | File: large.pdf (15MB) | 1. POST upload large.pdf | 400 hoặc 413 "File vượt quá kích thước cho phép" | Trung bình | Boundary |
| UPL-10 | Upload — hồ sơ không tồn tại | Không có hồ sơ với id này | applicationId: "INVALID_ID", file hợp lệ | 1. POST /api/applications/INVALID_ID/attachments | 404 "Không tìm thấy hồ sơ: INVALID_ID" | Cao | Negative |
| UPL-11 | Upload — CITIZEN khác | CITIZEN B đăng nhập, hồ sơ của CITIZEN A | Token CITIZEN B, file hợp lệ, appId của A | 1. POST upload vào hồ sơ A bằng token B | 400 "Bạn không có quyền truy cập tài liệu của hồ sơ này" | Cao | Permission |
| UPL-12 | Upload — OFFICER được phép | OFFICER đăng nhập, hồ sơ bất kỳ | Token OFFICER, file hợp lệ | 1. POST upload với token OFFICER | success=true, upload thành công | Trung bình | Permission |
| UPL-13 | Upload — ADMIN được phép | ADMIN đăng nhập | Token ADMIN, file hợp lệ | 1. POST upload với token ADMIN | success=true | Trung bình | Permission |
| UPL-14 | Upload — kiểm tra file lưu disk | Hồ sơ tồn tại | File: test.pdf | 1. POST upload 2. Kiểm tra response.duongDanFile 3. Verify file tồn tại trên disk | duongDanFile có dạng UUID.pdf, file physical tồn tại trong thư mục uploads | Trung bình | Positive |

## B. Danh Sách & Xóa Tài Liệu

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| UPL-15 | Danh sách tài liệu hồ sơ | Hồ sơ có 3 tài liệu, CITIZEN chủ hồ sơ | applicationId | 1. GET /api/applications/{appId}/attachments | Trả list 3 tài liệu, mỗi item có id, tenTaiLieu, loaiFile, duongDanFile | Trung bình | Positive |
| UPL-16 | Danh sách — CITIZEN khác | CITIZEN B, hồ sơ của A | Token B, appId của A | 1. GET /api/applications/{appIdA}/attachments | 400 "Bạn không có quyền" | Cao | Permission |
| UPL-17 | Xóa tài liệu — chủ hồ sơ | CITIZEN chủ hồ sơ, tài liệu tồn tại | attachmentId | 1. DELETE /api/attachments/{id} 2. Verify file disk bị xóa 3. Verify metadata MongoDB bị xóa | success, file disk xóa, metadata xóa | Trung bình | Positive |
| UPL-18 | Xóa tài liệu — CITIZEN khác | CITIZEN B, tài liệu thuộc hồ sơ A | Token B, attachmentId thuộc A | 1. DELETE /api/attachments/{id} | 400 "Bạn không có quyền" | Cao | Permission |
| UPL-19 | Chi tiết tài liệu | Tài liệu tồn tại | attachmentId | 1. GET /api/attachments/{id} | Trả đầy đủ: id, tenTaiLieu, duongDanFile, loaiFile, hoSoHoTroId, kichThuoc | Trung bình | Positive |

## C. OCR Tài Liệu

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| OCR-01 | OCR PDF thành công | Tài liệu PDF đã upload, OFFICER đăng nhập | attachmentId (PDF) | 1. POST /api/attachments/{id}/ocr 2. Kiểm tra response 3. GET /api/attachments/{id} | Response có attachmentId, tenTaiLieu, ketQuaOcr (text), processedAt. Tài liệu MongoDB có ketQuaOcr != null | Cao | Positive |
| OCR-02 | OCR PNG thành công | Tài liệu PNG đã upload, ADMIN đăng nhập | attachmentId (PNG) | 1. POST /api/attachments/{id}/ocr | Response có ketQuaOcr, loaiFile="image/png" | Trung bình | Positive |
| OCR-03 | OCR — tài liệu không tồn tại | Không có tài liệu | attachmentId: "INVALID" | 1. POST /api/attachments/INVALID/ocr | 404 "Không tìm thấy tài liệu: INVALID" | Trung bình | Negative |
| OCR-04 | OCR — loại file không hỗ trợ | Tài liệu có loaiFile khác (giả sử bypass upload) | Tài liệu với loaiFile="text/plain" | 1. POST /api/attachments/{id}/ocr | 400 "Loại file không hỗ trợ OCR: text/plain. Chỉ hỗ trợ: PDF, PNG, JPG, JPEG" | Trung bình | Negative |
| OCR-05 | OCR — CITIZEN không được chạy | CITIZEN đăng nhập | Token CITIZEN | 1. POST /api/attachments/{id}/ocr | 403 Forbidden | Cao | Permission |
| OCR-06 | OCR — lưu kết quả vào MongoDB | Tài liệu PDF, OFFICER | attachmentId | 1. POST /api/attachments/{id}/ocr 2. Verify DB: tìm tài liệu bằng id, kiểm tra field ketQuaOcr | ketQuaOcr != null, ketQuaOcr chứa text trích xuất từ file | Cao | Business Rule |

---

## Tổng hợp

| Nhóm | Số TC | Positive | Negative | Boundary | Permission | Business Rule |
|------|-------|----------|----------|----------|------------|---------------|
| Upload file | 14 | 6 | 3 | 2 | 3 | 0 |
| Danh sách & Xóa | 5 | 2 | 0 | 0 | 2 | 1 |
| OCR | 6 | 2 | 2 | 0 | 1 | 1 |
| **Tổng** | **25** | **10** | **5** | **2** | **6** | **2** |

## Luồng upload → OCR

```
CITIZEN upload file (pdf/png/jpg/jpeg)
        │
        ├── Validate: format, size, hồ sơ tồn tại, ownership
        ├── Lưu file → disk (uploads/UUID.ext)
        └── Lưu metadata → MongoDB (attachments collection)
                │
                ▼
OFFICER/ADMIN chạy OCR
        │
        ├── Validate: tài liệu tồn tại, loại file hỗ trợ OCR
        ├── Đọc file từ disk
        ├── Gọi OcrService.extractText()
        └── Lưu ketQuaOcr vào tài liệu MongoDB
```

## Dữ liệu test files

| File | Kích thước | MIME Type | Mục đích |
|------|-----------|-----------|----------|
| cmnd.pdf | 500KB | application/pdf | Upload + OCR hợp lệ |
| giayxacnhan.png | 1MB | image/png | Upload + OCR hợp lệ |
| photo.jpg | 800KB | image/jpg | Upload hợp lệ |
| scan.jpeg | 600KB | image/jpeg | Upload hợp lệ |
| virus.exe | 100KB | application/x-msdownload | Bị từ chối |
| document.docx | 200KB | application/vnd.openxmlformats | Bị từ chối |
| archive.zip | 300KB | application/zip | Bị từ chối |
| empty.pdf | 0 bytes | — | Boundary: file rỗng |
| large.pdf | 15MB | application/pdf | Boundary: vượt kích thước |
