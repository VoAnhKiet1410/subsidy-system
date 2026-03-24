# Test Cases Chuyên Sâu — Module Hồ Sơ Hỗ Trợ

> Tổng số: **40 test cases** | Ngày tạo: 2026-03-24
> Luồng trạng thái: `DRAFT → SUBMITTED → UNDER_REVIEW → APPROVED / REJECTED → PAID`

---

## A. Tạo Hồ Sơ

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| HS-01 | Tạo hồ sơ mới | CITIZEN đăng nhập, có đối tượng "DT-001", chương trình "CT-001" | doiTuongId: "DT-001", chuongTrinhId: "CT-001", moTa: "Xin hỗ trợ" | 1. POST /api/applications với body hợp lệ 2. Kiểm tra response | success=true, trangThai="DRAFT", nguoiDungId=userId hiện tại, ngayNopHoSo=null | Cao | Positive |
| HS-02 | Tạo hồ sơ — doiTuongId không tồn tại | CITIZEN đăng nhập | doiTuongId: "INVALID_ID", chuongTrinhId: "CT-001" | 1. POST /api/applications | 400 "Đối tượng hưởng trợ cấp không tồn tại" | Cao | Negative |
| HS-03 | Tạo hồ sơ — chuongTrinhId không tồn tại | CITIZEN đăng nhập | doiTuongId: "DT-001", chuongTrinhId: "INVALID_ID" | 1. POST /api/applications | 400 "Chương trình trợ cấp không tồn tại" | Cao | Negative |
| HS-04 | Tạo hồ sơ — thiếu doiTuongId | CITIZEN đăng nhập | doiTuongId: null, chuongTrinhId: "CT-001" | 1. POST /api/applications thiếu doiTuongId | Hồ sơ DRAFT được tạo (cho phép thiếu khi DRAFT, validate khi submit) | Trung bình | Boundary |
| HS-05 | Tạo hồ sơ — OFFICER tạo thay CITIZEN | OFFICER đăng nhập | Body hợp lệ kèm nguoiDungId | 1. POST /api/applications với token OFFICER | Tạo thành công (ADMIN/OFFICER có quyền) | Trung bình | Permission |

## B. Submit Hồ Sơ (DRAFT → SUBMITTED)

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| HS-06 | Submit thành công | Hồ sơ DRAFT đầy đủ (doiTuongId + chuongTrinhId) | id hồ sơ DRAFT | 1. PATCH /api/applications/{id}/submit | trangThai="SUBMITTED", ngayNopHoSo=ngày hiện tại | Cao | Business Rule |
| HS-07 | Submit — thiếu doiTuongId | Hồ sơ DRAFT có doiTuongId=null | id hồ sơ thiếu đối tượng | 1. PATCH /api/applications/{id}/submit | 400 "Thiếu thông tin đối tượng hưởng trợ cấp" | Cao | Business Rule |
| HS-08 | Submit — thiếu chuongTrinhId | Hồ sơ DRAFT có chuongTrinhId=null | id hồ sơ thiếu chương trình | 1. PATCH /api/applications/{id}/submit | 400 "Thiếu thông tin chương trình trợ cấp" | Cao | Business Rule |
| HS-09 | Submit hồ sơ đã SUBMITTED | Hồ sơ trạng thái SUBMITTED | id hồ sơ SUBMITTED | 1. PATCH /api/applications/{id}/submit | 400 "Chỉ submit hồ sơ ở trạng thái DRAFT" | Cao | Business Rule |
| HS-10 | Submit hồ sơ APPROVED | Hồ sơ trạng thái APPROVED | id hồ sơ APPROVED | 1. PATCH submit | 400 "Trạng thái hiện tại: APPROVED" | Trung bình | Business Rule |
| HS-11 | Submit — CITIZEN khác | CITIZEN B, hồ sơ của CITIZEN A | Token B, id hồ sơ của A | 1. PATCH submit | 400 "Bạn không có quyền thao tác hồ sơ này" | Cao | Permission |

## C. Xét Duyệt (SUBMITTED → UNDER_REVIEW)

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| HS-12 | Bắt đầu xét duyệt | Hồ sơ SUBMITTED, OFFICER đăng nhập | id hồ sơ SUBMITTED | 1. PATCH /api/applications/{id}/under-review | trangThai="UNDER_REVIEW", reviewedBy=username OFFICER, thông báo INFO gửi đến CITIZEN | Cao | Business Rule |
| HS-13 | Xét duyệt hồ sơ DRAFT | Hồ sơ DRAFT | id hồ sơ DRAFT | 1. PATCH under-review | 400 "Chỉ hồ sơ SUBMITTED mới được chuyển sang UNDER_REVIEW" | Cao | Business Rule |
| HS-14 | Xét duyệt hồ sơ APPROVED | Hồ sơ APPROVED | id hồ sơ APPROVED | 1. PATCH under-review | 400 "Trạng thái hiện tại: APPROVED" | Trung bình | Business Rule |
| HS-15 | CITIZEN bắt đầu xét duyệt | CITIZEN đăng nhập | Token CITIZEN | 1. PATCH under-review | 403 Forbidden | Cao | Permission |
| HS-16 | ACCOUNTANT bắt đầu xét duyệt | ACCOUNTANT đăng nhập | Token ACCOUNTANT | 1. PATCH under-review | 403 Forbidden | Trung bình | Permission |

## D. Phê Duyệt (UNDER_REVIEW → APPROVED)

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| HS-17 | Phê duyệt thành công | Hồ sơ UNDER_REVIEW, OFFICER | id hồ sơ UNDER_REVIEW | 1. PATCH /api/applications/{id}/approve 2. GET chi tiết hồ sơ 3. GET notifications CITIZEN | trangThai="APPROVED", approvedBy=username, approvedAt=now, lyDoTuChoi=null, thông báo SUCCESS | Cao | Business Rule |
| HS-18 | Phê duyệt hồ sơ SUBMITTED | Hồ sơ SUBMITTED | id hồ sơ SUBMITTED | 1. PATCH approve | 400 "Chỉ hồ sơ UNDER_REVIEW mới được phê duyệt" | Cao | Business Rule |
| HS-19 | Phê duyệt hồ sơ REJECTED | Hồ sơ REJECTED | id hồ sơ REJECTED | 1. PATCH approve | 400 "Trạng thái hiện tại: REJECTED" | Trung bình | Business Rule |
| HS-20 | Phê duyệt xóa lyDoTuChoi cũ | Hồ sơ UNDER_REVIEW, từng bị reject (có lyDoTuChoi) | id hồ sơ có lyDoTuChoi != null | 1. PATCH approve 2. GET chi tiết | lyDoTuChoi = null (đã xóa) | Cao | Business Rule |
| HS-21 | CITIZEN phê duyệt | CITIZEN đăng nhập | Token CITIZEN | 1. PATCH approve | 403 Forbidden | Cao | Permission |

## E. Từ Chối (UNDER_REVIEW → REJECTED)

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| HS-22 | Từ chối thành công | Hồ sơ UNDER_REVIEW, OFFICER | lyDoTuChoi: "Thiếu giấy chứng nhận hộ nghèo" | 1. PATCH /api/applications/{id}/reject với body 2. GET chi tiết 3. GET notifications CITIZEN | trangThai="REJECTED", lyDoTuChoi="Thiếu giấy...", thông báo WARNING kèm lý do | Cao | Business Rule |
| HS-23 | Từ chối — thiếu lý do | Hồ sơ UNDER_REVIEW | lyDoTuChoi: "" | 1. PATCH reject với body rỗng | 400 "Lý do từ chối không được để trống" | Cao | Business Rule |
| HS-24 | Từ chối — không gửi body | Hồ sơ UNDER_REVIEW | Body: null | 1. PATCH reject không gửi body | 400 validation error | Trung bình | Boundary |
| HS-25 | Từ chối hồ sơ DRAFT | Hồ sơ DRAFT | id hồ sơ DRAFT | 1. PATCH reject | 400 "Chỉ hồ sơ UNDER_REVIEW mới được từ chối" | Trung bình | Business Rule |
| HS-26 | CITIZEN từ chối | CITIZEN đăng nhập | Token CITIZEN | 1. PATCH reject | 403 Forbidden | Cao | Permission |

## F. Sửa / Xóa Hồ Sơ

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| HS-27 | Sửa hồ sơ DRAFT | CITIZEN, hồ sơ DRAFT | Cập nhật moTa: "Cập nhật mô tả" | 1. PUT /api/applications/{id} | Hồ sơ được cập nhật, trangThai vẫn DRAFT | Trung bình | Positive |
| HS-28 | Sửa hồ sơ SUBMITTED | CITIZEN, hồ sơ SUBMITTED | Cập nhật moTa | 1. PUT /api/applications/{id} | 400 "Chỉ được sửa hồ sơ ở trạng thái DRAFT" | Trung bình | Business Rule |
| HS-29 | Xóa hồ sơ DRAFT | CITIZEN, hồ sơ DRAFT | id hồ sơ DRAFT | 1. DELETE /api/applications/{id} | Xóa thành công | Trung bình | Positive |
| HS-30 | Xóa hồ sơ người khác | CITIZEN B, hồ sơ của CITIZEN A | Token B, id hồ sơ A | 1. DELETE /api/applications/{id_of_A} | 400 "Bạn không có quyền thao tác hồ sơ này" | Cao | Permission |

## G. Tìm Kiếm, Lọc, Phân Trang

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| HS-31 | Lọc theo trangThai | Có 5 hồ sơ: 2 DRAFT, 2 SUBMITTED, 1 APPROVED | trangThai=SUBMITTED | 1. GET /api/applications?trangThai=SUBMITTED | Trả 2 hồ sơ SUBMITTED | Trung bình | Positive |
| HS-32 | Lọc theo chuongTrinhTroCapId | Có hồ sơ thuộc 2 chương trình | chuongTrinhTroCapId=CT-001 | 1. GET /api/applications?chuongTrinhTroCapId=CT-001 | Chỉ trả hồ sơ thuộc CT-001 | Trung bình | Positive |
| HS-33 | Lọc theo khoảng ngày | Có hồ sơ nộp từ 01/2024 → 06/2024 | fromNgayNopHoSo=2024-03-01, toNgayNopHoSo=2024-03-31 | 1. GET /api/applications?from...&to... | Chỉ trả hồ sơ nộp tháng 3/2024 | Trung bình | Positive |
| HS-34 | Kết hợp nhiều filter | Có data đa dạng | trangThai=APPROVED, chuongTrinhTroCapId=CT-001 | 1. GET /api/applications?trangThai=APPROVED&chuongTrinhTroCapId=CT-001 | Trả hồ sơ khớp CẢ 2 điều kiện (AND) | Cao | Positive |
| HS-35 | Phân trang | Có 25 hồ sơ | page=0, size=10 | 1. GET /api/applications?page=0&size=10 | items=10, totalItems=25, totalPages=3 | Trung bình | Positive |
| HS-36 | Sort tùy chỉnh | Có nhiều hồ sơ | sort=ngayNopHoSo,asc | 1. GET /api/applications?sort=ngayNopHoSo,asc | Kết quả sắp tăng dần theo ngày nộp | Thấp | Positive |
| HS-37 | CITIZEN search chỉ thấy của mình | CITIZEN A có 3 HS, CITIZEN B có 2 HS | Token CITIZEN A, không truyền nguoiDungId | 1. GET /api/applications (CITIZEN A) | Trả đúng 3 hồ sơ của A, không thấy hồ sơ B | Cao | Permission |
| HS-38 | ADMIN search xem tất cả | ADMIN, có 10 hồ sơ của nhiều user | Token ADMIN | 1. GET /api/applications | Trả tất cả 10 hồ sơ | Cao | Permission |

## H. Luồng Trạng Thái Đầy Đủ (End-to-End)

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| HS-39 | Luồng hoàn chỉnh đến APPROVED | CITIZEN, OFFICER, đối tượng + chương trình tồn tại | Dữ liệu hợp lệ | 1. CITIZEN POST tạo hồ sơ (→ DRAFT) 2. CITIZEN PATCH submit (→ SUBMITTED) 3. OFFICER PATCH under-review (→ UNDER_REVIEW) 4. Kiểm tra notification INFO 5. OFFICER PATCH approve (→ APPROVED) 6. Kiểm tra notification SUCCESS | Luồng 4 bước thành công, 2 thông báo cho CITIZEN | Cao | Business Rule |
| HS-40 | Luồng reject rồi re-approve | CITIZEN, OFFICER | Dữ liệu hợp lệ | 1. Tạo → submit → under-review → reject (kèm lý do "Thiếu CMND") 2. Kiểm tra lyDoTuChoi = "Thiếu CMND" 3. Giả sử CITIZEN bổ sung tài liệu 4. OFFICER chuyển lại under-review → approve 5. Kiểm tra lyDoTuChoi = null | After approve: lyDoTuChoi bị xóa, trangThai = APPROVED, 4 thông báo tổng | Cao | Business Rule |

---

## Tổng hợp

| Nhóm | Số TC | Positive | Negative | Boundary | Permission | Business Rule |
|------|-------|----------|----------|----------|------------|---------------|
| Tạo hồ sơ | 5 | 1 | 2 | 1 | 1 | 0 |
| Submit | 6 | 0 | 0 | 0 | 1 | 5 |
| Xét duyệt | 5 | 0 | 0 | 0 | 2 | 3 |
| Phê duyệt | 5 | 0 | 0 | 0 | 1 | 4 |
| Từ chối | 5 | 0 | 0 | 1 | 1 | 3 |
| Sửa/Xóa | 4 | 2 | 0 | 0 | 1 | 1 |
| Tìm kiếm/Lọc | 8 | 5 | 0 | 0 | 2 | 1 |
| E2E luồng | 2 | 0 | 0 | 0 | 0 | 2 |
| **Tổng** | **40** | **8** | **2** | **2** | **9** | **19** |

## Ma trận chuyển trạng thái — Test Coverage

| Từ \ Đến | DRAFT | SUBMITTED | UNDER_REVIEW | APPROVED | REJECTED | PAID |
|-----------|-------|-----------|--------------|----------|----------|------|
| **DRAFT** | — | HS-06 ✅ | HS-13 ❌ | HS-10 ❌ | HS-25 ❌ | — |
| **SUBMITTED** | — | HS-09 ❌ | HS-12 ✅ | HS-18 ❌ | — | — |
| **UNDER_REVIEW** | — | — | — | HS-17 ✅ | HS-22 ✅ | — |
| **APPROVED** | — | — | HS-14 ❌ | — | HS-19 ❌ | PAY-06 ✅ |
| **REJECTED** | — | — | HS-40 ✅ | HS-40 ✅ | — | — |
| **PAID** | — | — | — | — | — | — |

> ✅ = chuyển hợp lệ (test positive) | ❌ = chuyển bị chặn (test negative)
