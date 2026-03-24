# Test Cases Nghiệp Vụ — Hệ Thống Quản Lý Trợ Cấp Xã Hội

> Tổng số: **95 test cases** | 11 modules | Ngày tạo: 2026-03-24

---

## Module 1: Đăng ký & Đăng nhập (Auth)

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| AUTH-01 | Đăng ký | Đăng ký thành công | Không có tài khoản trùng | username: "citizen1", email: "c1@test.com", password: "Pass@123" | 1. POST /api/auth/register với body hợp lệ 2. Kiểm tra response | success=true, user được tạo, role mặc định CITIZEN | Cao | Positive |
| AUTH-02 | Đăng ký | Username trùng | User "citizen1" đã tồn tại | username: "citizen1" (trùng) | 1. POST /api/auth/register với username đã tồn tại | 400 Bad Request, message "Username đã tồn tại" | Cao | Negative |
| AUTH-03 | Đăng ký | Email trùng | Email "c1@test.com" đã tồn tại | email: "c1@test.com" (trùng) | 1. POST /api/auth/register với email đã tồn tại | 400 Bad Request, message "Email đã tồn tại" | Cao | Negative |
| AUTH-04 | Đăng ký | Thiếu username | Không | username: "", email: "x@test.com" | 1. POST /api/auth/register thiếu username | 400 Bad Request, validation error | Trung bình | Boundary |
| AUTH-05 | Đăng ký | Thiếu email | Không | username: "user2", email: "" | 1. POST /api/auth/register thiếu email | 400 Bad Request, validation error | Trung bình | Boundary |
| AUTH-06 | Đăng nhập | Đăng nhập thành công | User "citizen1" đã đăng ký | username: "citizen1", password: "Pass@123" | 1. POST /api/auth/login 2. Kiểm tra response có accessToken và refreshToken | success=true, trả accessToken + refreshToken | Cao | Positive |
| AUTH-07 | Đăng nhập | Sai mật khẩu | User tồn tại | username: "citizen1", password: "wrong" | 1. POST /api/auth/login với mật khẩu sai | 401 Unauthorized | Cao | Negative |
| AUTH-08 | Đăng nhập | User không tồn tại | Không | username: "unknown", password: "any" | 1. POST /api/auth/login với user không tồn tại | 401 Unauthorized | Trung bình | Negative |
| AUTH-09 | Refresh token | Làm mới token thành công | Có refreshToken hợp lệ | refreshToken từ login | 1. POST /api/auth/refresh với refreshToken hợp lệ | Trả accessToken mới | Cao | Positive |
| AUTH-10 | Refresh token | Token hết hạn | refreshToken đã hết hạn | refreshToken expired | 1. POST /api/auth/refresh với token hết hạn | 401 Unauthorized | Trung bình | Negative |

## Module 2: Quản lý Người dùng

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| USER-01 | Danh sách user | ADMIN xem danh sách | Đăng nhập ADMIN | Token ADMIN | 1. GET /api/users với token ADMIN | Trả danh sách user phân trang | Cao | Positive |
| USER-02 | Danh sách user | CITIZEN không được xem | Đăng nhập CITIZEN | Token CITIZEN | 1. GET /api/users với token CITIZEN | 403 Forbidden | Cao | Permission |
| USER-03 | Chi tiết user | Xem thông tin user | ADMIN, user tồn tại | userId hợp lệ | 1. GET /api/users/{id} | Trả thông tin user | Trung bình | Positive |
| USER-04 | Tạo user | ADMIN tạo user mới | Đăng nhập ADMIN | Body: username, email, password | 1. POST /api/users với đầy đủ thông tin | User được tạo thành công | Trung bình | Positive |
| USER-05 | Gán role | ADMIN gán role cho user | ADMIN, user và role tồn tại | userId, roleCodes: ["OFFICER"] | 1. POST /api/users/{id}/roles | User có thêm role OFFICER | Cao | Positive |
| USER-06 | Xóa user | CITIZEN không được xóa | Đăng nhập CITIZEN | Token CITIZEN | 1. DELETE /api/users/{id} với token CITIZEN | 403 Forbidden | Trung bình | Permission |

## Module 3: Quản lý Vai trò

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| ROLE-01 | Danh sách role | ADMIN xem roles | Đăng nhập ADMIN | Token ADMIN | 1. GET /api/roles | Trả danh sách roles: ADMIN, OFFICER, CITIZEN, ACCOUNTANT | Trung bình | Positive |
| ROLE-02 | Tạo role | ADMIN tạo role mới | Đăng nhập ADMIN | maVaiTro: "SUPERVISOR", tenVaiTro: "Giám sát" | 1. POST /api/roles | Role được tạo thành công | Trung bình | Positive |
| ROLE-03 | Tạo role | OFFICER không được tạo | Đăng nhập OFFICER | Token OFFICER | 1. POST /api/roles với token OFFICER | 403 Forbidden | Cao | Permission |
| ROLE-04 | Xóa role | CITIZEN không được xóa | Đăng nhập CITIZEN | Token CITIZEN | 1. DELETE /api/roles/{id} với token CITIZEN | 403 Forbidden | Trung bình | Permission |

## Module 4: Quản lý Đối tượng Hưởng Trợ Cấp

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| BEN-01 | Tạo đối tượng | ADMIN tạo thành công | Đăng nhập ADMIN | tenDoiTuong: "Người khuyết tật", moTa: "Hỗ trợ NKT" | 1. POST /api/beneficiary-groups | Đối tượng được tạo | Cao | Positive |
| BEN-02 | Tạo đối tượng | thiếu tên | Đăng nhập ADMIN | tenDoiTuong: "" | 1. POST /api/beneficiary-groups thiếu tên | 400 validation error | Trung bình | Boundary |
| BEN-03 | Tạo đối tượng | CITIZEN không được tạo | Đăng nhập CITIZEN | Token CITIZEN | 1. POST /api/beneficiary-groups | 403 Forbidden | Cao | Permission |
| BEN-04 | Tìm kiếm | Tìm theo tên | Có dữ liệu sẵn | keyword: "khuyết tật" | 1. GET /api/beneficiary-groups?tenDoiTuong=khuyết tật | Trả kết quả khớp | Trung bình | Positive |
| BEN-05 | Xóa đối tượng | OFFICER xóa thành công | Đăng nhập OFFICER, có đối tượng | id hợp lệ | 1. DELETE /api/beneficiary-groups/{id} | Xóa thành công | Trung bình | Positive |

## Module 5: Quản lý Nguồn Quỹ

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| FUND-01 | Tạo nguồn quỹ | ADMIN tạo thành công | Đăng nhập ADMIN | tenNguonQuy: "NSNN 2024", tongNganSach: 1000000000 | 1. POST /api/fund-sources | Nguồn quỹ được tạo, conLai = tongNganSach | Cao | Positive |
| FUND-02 | Tạo nguồn quỹ | OFFICER không được tạo | Đăng nhập OFFICER | Token OFFICER | 1. POST /api/fund-sources | 403 Forbidden | Cao | Permission |
| FUND-03 | Kiểm tra số dư | conLai chính xác sau chi trả | Nguồn quỹ có conLai = 500tr | Sau khi chi trả 100tr | 1. GET /api/fund-sources/{id} 2. Kiểm tra conLai | conLai = 400tr, daSuDung tăng 100tr | Cao | Business Rule |

## Module 6: Quản lý Chương Trình Trợ Cấp

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| PROG-01 | Tạo chương trình | ADMIN tạo thành công | Nguồn quỹ tồn tại | tenChuongTrinh: "HT Thất nghiệp", nguonQuyId, ngayBatDau, ngayKetThuc | 1. POST /api/programs | Chương trình được tạo | Cao | Positive |
| PROG-02 | Tạo chương trình | ngayBatDau > ngayKetThuc | Đăng nhập ADMIN | ngayBatDau: 2025-12-01, ngayKetThuc: 2025-01-01 | 1. POST /api/programs với ngày sai | 400 Bad Request | Trung bình | Boundary |
| PROG-03 | Tạo chương trình | nguonQuyId không tồn tại | Đăng nhập ADMIN | nguonQuyId: "invalid_id" | 1. POST /api/programs | 400 "Nguồn quỹ không tồn tại" | Trung bình | Negative |
| PROG-04 | Tạo chương trình | CITIZEN không được tạo | Đăng nhập CITIZEN | Token CITIZEN | 1. POST /api/programs | 403 Forbidden | Cao | Permission |
| PROG-05 | Lọc chương trình | Lọc theo trạng thái | Có data sẵn | trangThai: "OPEN" | 1. GET /api/programs?trangThai=OPEN | Chỉ trả chương trình OPEN | Trung bình | Positive |

## Module 7: Quản lý Hồ Sơ Hỗ Trợ

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| APP-01 | Tạo hồ sơ | CITIZEN tạo DRAFT | Đăng nhập CITIZEN, có đối tượng + chương trình | doiTuongId, chuongTrinhId | 1. POST /api/applications | Hồ sơ được tạo, trangThai = DRAFT | Cao | Positive |
| APP-02 | Tạo hồ sơ | doiTuongId không tồn tại | Đăng nhập CITIZEN | doiTuongId: "invalid" | 1. POST /api/applications | 400 "Đối tượng không tồn tại" | Trung bình | Negative |
| APP-03 | Tạo hồ sơ | chuongTrinhId không tồn tại | Đăng nhập CITIZEN | chuongTrinhId: "invalid" | 1. POST /api/applications | 400 "Chương trình không tồn tại" | Trung bình | Negative |
| APP-04 | Submit hồ sơ | DRAFT → SUBMITTED | Hồ sơ DRAFT đầy đủ info | id hồ sơ DRAFT | 1. PATCH /api/applications/{id}/submit | trangThai = SUBMITTED, ngayNopHoSo = today | Cao | Business Rule |
| APP-05 | Submit hồ sơ | Thiếu doiTuongId | Hồ sơ DRAFT thiếu đối tượng | Hồ sơ chưa có doiTuongId | 1. PATCH /api/applications/{id}/submit | 400 "Thiếu thông tin đối tượng" | Cao | Business Rule |
| APP-06 | Submit hồ sơ | Hồ sơ không phải DRAFT | Hồ sơ SUBMITTED | id hồ sơ SUBMITTED | 1. PATCH /api/applications/{id}/submit | 400 "Chỉ submit hồ sơ DRAFT" | Cao | Business Rule |
| APP-07 | Xem hồ sơ | CITIZEN chỉ xem của mình | CITIZEN A có hồ sơ, CITIZEN B khác | Token CITIZEN B, id hồ sơ của A | 1. GET /api/applications (CITIZEN B) 2. Kiểm tra kết quả | Không thấy hồ sơ của CITIZEN A | Cao | Permission |
| APP-08 | Xem hồ sơ | ADMIN xem tất cả | Đăng nhập ADMIN | Token ADMIN | 1. GET /api/applications | Trả tất cả hồ sơ | Cao | Permission |
| APP-09 | Tìm kiếm | Lọc theo trangThai | Có nhiều hồ sơ | trangThai: "APPROVED" | 1. GET /api/applications?trangThai=APPROVED | Chỉ trả hồ sơ APPROVED | Trung bình | Positive |
| APP-10 | Tìm kiếm | Lọc theo khoảng ngày | Có hồ sơ từ 01-06/2024 | fromNgayNopHoSo: 2024-03-01, toNgayNopHoSo: 2024-03-31 | 1. GET /api/applications?from...&to... | Chỉ trả hồ sơ nộp trong tháng 3 | Trung bình | Positive |
| APP-11 | Tìm kiếm | Kết hợp nhiều filter | Có data đa dạng | trangThai=SUBMITTED, chuongTrinhTroCapId=X | 1. GET /api/applications?trangThai=...&chuongTrinhTroCapId=... | Trả hồ sơ khớp cả 2 điều kiện | Trung bình | Positive |
| APP-12 | Tìm kiếm | Sort theo ngayNopHoSo ASC | Có data | sort=ngayNopHoSo,asc | 1. GET /api/applications?sort=ngayNopHoSo,asc | Kết quả sắp xếp tăng dần | Thấp | Positive |
| APP-13 | Sửa hồ sơ | CITIZEN sửa hồ sơ DRAFT | Hồ sơ DRAFT của CITIZEN | Cập nhật moTa | 1. PUT /api/applications/{id} | Hồ sơ được cập nhật | Trung bình | Positive |
| APP-14 | Xóa hồ sơ | Xóa hồ sơ DRAFT | Hồ sơ DRAFT | id hồ sơ DRAFT | 1. DELETE /api/applications/{id} | Xóa thành công | Trung bình | Positive |
| APP-15 | Xóa hồ sơ | CITIZEN xóa hồ sơ người khác | CITIZEN B, hồ sơ của A | Token CITIZEN B | 1. DELETE /api/applications/{id_of_A} | 400 "Không có quyền" | Cao | Permission |

## Module 8: Duyệt / Từ Chối Hồ Sơ

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| REV-01 | Bắt đầu xét duyệt | SUBMITTED → UNDER_REVIEW | Hồ sơ SUBMITTED, đăng nhập OFFICER | id hồ sơ SUBMITTED | 1. PATCH /api/applications/{id}/under-review | trangThai = UNDER_REVIEW, thông báo INFO cho CITIZEN | Cao | Business Rule |
| REV-02 | Bắt đầu xét duyệt | Hồ sơ không phải SUBMITTED | Hồ sơ DRAFT | id hồ sơ DRAFT | 1. PATCH /api/applications/{id}/under-review | 400 "Chỉ hồ sơ SUBMITTED" | Cao | Business Rule |
| REV-03 | Phê duyệt | UNDER_REVIEW → APPROVED | Hồ sơ UNDER_REVIEW, OFFICER | id hồ sơ | 1. PATCH /api/applications/{id}/approve | trangThai = APPROVED, lyDoTuChoi = null, thông báo SUCCESS | Cao | Business Rule |
| REV-04 | Phê duyệt | Hồ sơ không phải UNDER_REVIEW | Hồ sơ SUBMITTED | id hồ sơ SUBMITTED | 1. PATCH /api/applications/{id}/approve | 400 "Chỉ hồ sơ UNDER_REVIEW" | Cao | Business Rule |
| REV-05 | Từ chối | UNDER_REVIEW → REJECTED | Hồ sơ UNDER_REVIEW, OFFICER | lyDoTuChoi: "Thiếu giấy tờ" | 1. PATCH /api/applications/{id}/reject với body | trangThai = REJECTED, lyDoTuChoi được lưu, thông báo WARNING | Cao | Business Rule |
| REV-06 | Từ chối | Thiếu lý do từ chối | Hồ sơ UNDER_REVIEW | lyDoTuChoi: "" | 1. PATCH /api/applications/{id}/reject không có lý do | 400 "Lý do từ chối không được để trống" | Cao | Business Rule |
| REV-07 | Phê duyệt | CITIZEN không được duyệt | Đăng nhập CITIZEN | Token CITIZEN | 1. PATCH /api/applications/{id}/approve | 403 Forbidden | Cao | Permission |
| REV-08 | Từ chối | CITIZEN không được từ chối | Đăng nhập CITIZEN | Token CITIZEN | 1. PATCH /api/applications/{id}/reject | 403 Forbidden | Cao | Permission |
| REV-09 | Phê duyệt | Xóa lyDoTuChoi cũ | Hồ sơ từng bị REJECTED rồi re-review | Hồ sơ có lyDoTuChoi từ lần reject trước | 1. Chuyển lại UNDER_REVIEW 2. PATCH approve | trangThai = APPROVED, lyDoTuChoi = null | Trung bình | Business Rule |
| REV-10 | Thông báo | Tạo thông báo khi duyệt | Hồ sơ UNDER_REVIEW | id hồ sơ | 1. PATCH approve 2. Kiểm tra notifications của CITIZEN | Có thông báo SUCCESS "Hồ sơ đã được phê duyệt" | Cao | Business Rule |

## Module 9: Tài Liệu Đính Kèm

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| DOC-01 | Upload file | Upload PDF thành công | Hồ sơ tồn tại, CITIZEN chủ hồ sơ | File test.pdf (< 10MB) | 1. POST /api/applications/{id}/attachments multipart | File lưu disk, metadata lưu MongoDB | Cao | Positive |
| DOC-02 | Upload file | Upload PNG thành công | Hồ sơ tồn tại | File photo.png | 1. POST /api/applications/{id}/attachments | Upload thành công | Trung bình | Positive |
| DOC-03 | Upload file | File .exe bị từ chối | Hồ sơ tồn tại | File virus.exe | 1. POST /api/applications/{id}/attachments | 400 "Định dạng file không hợp lệ" | Cao | Negative |
| DOC-04 | Upload file | File .docx bị từ chối | Hồ sơ tồn tại | File doc.docx | 1. POST upload | 400 "Chỉ chấp nhận pdf, png, jpg, jpeg" | Trung bình | Negative |
| DOC-05 | Upload file | File > 10MB | Hồ sơ tồn tại | File large.pdf (15MB) | 1. POST upload file lớn | 400 hoặc 413 "Vượt quá kích thước" | Trung bình | Boundary |
| DOC-06 | Upload file | Hồ sơ không tồn tại | Không | applicationId: "invalid" | 1. POST /api/applications/invalid/attachments | 404 "Không tìm thấy hồ sơ" | Trung bình | Negative |
| DOC-07 | Danh sách tài liệu | Xem tài liệu hồ sơ | Hồ sơ có 2 file đính kèm | applicationId hợp lệ | 1. GET /api/applications/{id}/attachments | Trả 2 tài liệu | Trung bình | Positive |
| DOC-08 | Upload file | CITIZEN khác không được upload | CITIZEN B, hồ sơ của A | Token CITIZEN B | 1. POST upload vào hồ sơ của A | 400 "Không có quyền" | Cao | Permission |
| DOC-09 | Xóa tài liệu | Chủ hồ sơ xóa | CITIZEN chủ sở hữu | id tài liệu | 1. DELETE /api/attachments/{id} | Xóa file disk + metadata | Trung bình | Positive |

## Module 10: OCR Tài Liệu

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| OCR-01 | Chạy OCR | OCR PDF thành công | Tài liệu PDF đã upload, đăng nhập OFFICER | id tài liệu PDF | 1. POST /api/attachments/{id}/ocr | ketQuaOcr được lưu, trả OcrResponse | Cao | Positive |
| OCR-02 | Chạy OCR | OCR PNG thành công | Tài liệu PNG đã upload | id tài liệu PNG | 1. POST /api/attachments/{id}/ocr | Trả kết quả OCR | Trung bình | Positive |
| OCR-03 | Chạy OCR | File không hỗ trợ OCR | Tài liệu loại khác | Tài liệu có loaiFile không hỗ trợ | 1. POST /api/attachments/{id}/ocr | 400 "Loại file không hỗ trợ OCR" | Trung bình | Negative |
| OCR-04 | Chạy OCR | Tài liệu không tồn tại | Không | id: "invalid" | 1. POST /api/attachments/invalid/ocr | 404 "Không tìm thấy tài liệu" | Trung bình | Negative |
| OCR-05 | Chạy OCR | CITIZEN không được chạy | Đăng nhập CITIZEN | Token CITIZEN | 1. POST /api/attachments/{id}/ocr | 403 Forbidden | Cao | Permission |

## Module 11: Chi Trả Trợ Cấp

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| PAY-01 | Tạo chi trả | Tạo PENDING cho hồ sơ APPROVED | Hồ sơ APPROVED, đăng nhập ACCOUNTANT | hoSoHoTroId, soTien: 5000000, phuongThuc: "BANK_TRANSFER" | 1. POST /api/payments | Payment PENDING được tạo | Cao | Positive |
| PAY-02 | Tạo chi trả | Hồ sơ chưa APPROVED | Hồ sơ SUBMITTED | hoSoHoTroId (SUBMITTED) | 1. POST /api/payments | 400 "Chỉ tạo chi trả cho hồ sơ APPROVED" | Cao | Business Rule |
| PAY-03 | Tạo chi trả | soTien <= 0 | Đăng nhập ACCOUNTANT | soTien: -100 | 1. POST /api/payments | 400 "Số tiền phải lớn hơn 0" | Trung bình | Boundary |
| PAY-04 | Tạo chi trả | Thiếu phuongThuc | Đăng nhập ACCOUNTANT | phuongThuc: "" | 1. POST /api/payments | 400 "Phương thức không được để trống" | Trung bình | Boundary |
| PAY-05 | Tạo chi trả | CITIZEN không được tạo | Đăng nhập CITIZEN | Token CITIZEN | 1. POST /api/payments | 403 Forbidden | Cao | Permission |
| PAY-06 | Cập nhật trạng thái | PENDING → SUCCESS | Payment PENDING, nguồn quỹ đủ | trangThai: "SUCCESS" | 1. PATCH /api/payments/{id}/status 2. Kiểm tra hồ sơ 3. Kiểm tra nguồn quỹ | Payment SUCCESS, hồ sơ PAID, nguồn quỹ bị trừ, thông báo cho CITIZEN | Cao | Business Rule |
| PAY-07 | Cập nhật trạng thái | Vượt ngân sách | Payment PENDING, nguồn quỹ conLai = 1tr | soTien: 5tr, trangThai: "SUCCESS" | 1. PATCH status SUCCESS | 400 "Ngân sách không đủ" | Cao | Business Rule |
| PAY-08 | Cập nhật trạng thái | PENDING → FAILED | Payment PENDING | trangThai: "FAILED" | 1. PATCH /api/payments/{id}/status | Payment FAILED, hồ sơ vẫn APPROVED | Trung bình | Positive |
| PAY-09 | Cập nhật trạng thái | PENDING → CANCELLED | Payment PENDING | trangThai: "CANCELLED" | 1. PATCH status | Payment CANCELLED | Trung bình | Positive |
| PAY-10 | Cập nhật trạng thái | SUCCESS không đổi được | Payment đã SUCCESS | trangThai: "FAILED" | 1. PATCH status FAILED trên payment SUCCESS | 400 "Không thể thay đổi giao dịch đã thành công" | Cao | Business Rule |
| PAY-11 | Cập nhật trạng thái | Trạng thái không hợp lệ | Payment PENDING | trangThai: "INVALID" | 1. PATCH status INVALID | 400 "Trạng thái không hợp lệ" | Trung bình | Negative |
| PAY-12 | Nhất quán dữ liệu | Kiểm tra nguồn quỹ sau SUCCESS | Nguồn quỹ conLai = 500tr | Chi trả 100tr SUCCESS | 1. POST payment 2. PATCH SUCCESS 3. GET nguồn quỹ | conLai = 400tr, daSuDung tăng 100tr | Cao | Business Rule |
| PAY-13 | Nhất quán dữ liệu | Chương trình daChi tăng | Chương trình có daChi = 0 | Chi trả 100tr SUCCESS | 1. PATCH SUCCESS 2. GET chương trình | daChi = 100tr | Cao | Business Rule |

## Module 12: Thông Báo

| TC ID | Chức năng | Mục tiêu | Điều kiện tiên quyết | Dữ liệu test | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Loại |
|-------|-----------|----------|----------------------|---------------|-------------------|-------------------|---------|------|
| NTF-01 | Danh sách | Xem thông báo của mình | CITIZEN có 5 thông báo | Token CITIZEN | 1. GET /api/notifications | Trả 5 thông báo, phân trang | Cao | Positive |
| NTF-02 | Danh sách | Không xem thông báo người khác | CITIZEN A, thông báo của B | Token CITIZEN A | 1. GET /api/notifications | Không chứa thông báo của B | Cao | Permission |
| NTF-03 | Lọc | Lọc chưa đọc | Có 3 chưa đọc, 2 đã đọc | daDoc=false | 1. GET /api/notifications?daDoc=false | Trả 3 thông báo chưa đọc | Trung bình | Positive |
| NTF-04 | Đếm chưa đọc | Đếm chính xác | User có 4 thông báo chưa đọc | Token user | 1. GET /api/notifications/unread-count | data = 4 | Trung bình | Positive |
| NTF-05 | Đánh dấu đã đọc | Đánh dấu 1 thông báo | Thông báo chưa đọc | id thông báo | 1. PATCH /api/notifications/{id}/read | daDoc = true | Trung bình | Positive |
| NTF-06 | Đánh dấu đã đọc | Không đánh dấu thông báo người khác | Thông báo của user B | Token user A | 1. PATCH /api/notifications/{idOfB}/read | 400 "Không có quyền" | Cao | Permission |
| NTF-07 | Tạo thông báo | ADMIN tạo thủ công | Đăng nhập ADMIN | nguoiDungId, tieuDe, noiDung | 1. POST /api/notifications | Thông báo được tạo | Trung bình | Positive |
| NTF-08 | Tạo thông báo | CITIZEN không được tạo | Đăng nhập CITIZEN | Token CITIZEN | 1. POST /api/notifications | 403 Forbidden | Trung bình | Permission |
| NTF-09 | Xóa | Xóa thông báo của mình | Thông báo của user | id thông báo | 1. DELETE /api/notifications/{id} | Xóa thành công | Trung bình | Positive |
| NTF-10 | Thông báo tự động | Hệ thống tạo khi duyệt hồ sơ | Hồ sơ UNDER_REVIEW | OFFICER approve | 1. PATCH approve 2. GET notifications CITIZEN | Có thông báo SUCCESS mới | Cao | Business Rule |
| NTF-11 | Thông báo tự động | Hệ thống tạo khi chi trả | Payment SUCCESS | ACCOUNTANT mark SUCCESS | 1. PATCH payment SUCCESS 2. GET notifications CITIZEN | Có thông báo "Chi trả thành công" | Cao | Business Rule |

---

## Phụ Lục

### 1. Danh sách nghiệp vụ trọng yếu

| # | Nghiệp vụ | Mức độ rủi ro |
|---|-----------|---------------|
| 1 | Luồng trạng thái hồ sơ: DRAFT → SUBMITTED → UNDER_REVIEW → APPROVED/REJECTED → PAID | **Rất cao** |
| 2 | Chi trả SUCCESS: trừ ngân sách, chuyển hồ sơ PAID, tạo thông báo (3 bước phải đồng bộ) | **Rất cao** |
| 3 | Không cho phép chi trả vượt ngân sách còn lại | **Rất cao** |
| 4 | CITIZEN chỉ xem/sửa/xóa hồ sơ + thông báo của chính mình | **Cao** |
| 5 | Khi reject bắt buộc có lý do, khi approve phải xóa lý do cũ | **Cao** |
| 6 | Upload file chỉ chấp nhận pdf, png, jpg, jpeg — giới hạn 10MB | **Cao** |
| 7 | OCR chỉ OFFICER/ADMIN được chạy | **Trung bình** |
| 8 | Tạo thông báo tự động khi duyệt/từ chối/chi trả | **Trung bình** |

### 2. Test cases ưu tiên cao cần test trước (Top 20)

| Thứ tự | TC ID | Lý do ưu tiên |
|--------|-------|----------------|
| 1 | AUTH-01 | Đăng ký — cổng vào hệ thống |
| 2 | AUTH-06 | Đăng nhập — JWT token |
| 3 | APP-01 | CITIZEN tạo hồ sơ DRAFT |
| 4 | APP-04 | Submit hồ sơ (DRAFT → SUBMITTED) |
| 5 | REV-01 | Bắt đầu xét duyệt (SUBMITTED → UNDER_REVIEW) |
| 6 | REV-03 | Phê duyệt (UNDER_REVIEW → APPROVED) |
| 7 | REV-05 | Từ chối + lý do |
| 8 | REV-06 | Từ chối thiếu lý do (phải fail) |
| 9 | PAY-01 | Tạo chi trả cho hồ sơ APPROVED |
| 10 | PAY-06 | Chi trả SUCCESS → hồ sơ PAID + trừ ngân sách |
| 11 | PAY-07 | Vượt ngân sách (phải fail) |
| 12 | PAY-02 | Tạo chi trả cho hồ sơ chưa APPROVED (phải fail) |
| 13 | APP-07 | CITIZEN không xem hồ sơ người khác |
| 14 | NTF-02 | Không xem thông báo người khác |
| 15 | DOC-01 | Upload PDF thành công |
| 16 | DOC-03 | Upload .exe bị từ chối |
| 17 | AUTH-02 | Trùng username |
| 18 | PAY-12 | Nhất quán nguồn quỹ sau chi trả |
| 19 | NTF-10 | Thông báo tự động khi duyệt |
| 20 | REV-07 | CITIZEN không được duyệt |

### 3. Dữ liệu mẫu cần chuẩn bị cho QA

#### Users (4 tài khoản)

| Username | Password | Role | Mục đích |
|----------|----------|------|----------|
| admin | Admin@123 | ADMIN | Test quản lý hệ thống |
| officer1 | Officer@123 | OFFICER | Test xét duyệt hồ sơ, OCR |
| citizen1 | Citizen@123 | CITIZEN | Test nộp hồ sơ chính |
| accountant1 | Account@123 | ACCOUNTANT | Test chi trả trợ cấp |

#### Đối tượng hưởng trợ cấp (3 records)

| tenDoiTuong | moTa |
|-------------|------|
| Người khuyết tật | Hỗ trợ sinh hoạt cho NKT |
| Hộ nghèo | Hỗ trợ hộ nghèo, cận nghèo |
| Người cao tuổi | Hỗ trợ NCT từ 80 tuổi |

#### Nguồn quỹ (2 records)

| tenNguonQuy | tongNganSach | loai |
|-------------|-------------|------|
| Ngân sách Nhà nước 2024 | 1,000,000,000 | NGAN_SACH_NHA_NUOC |
| Quỹ tài trợ ABC | 500,000,000 | TAI_TRO |

#### Chương trình trợ cấp (2 records)

| tenChuongTrinh | nguonQuyId | ngayBatDau | ngayKetThuc | trangThai |
|----------------|-----------|------------|-------------|-----------|
| HT Thất nghiệp Q1/2024 | → NSNN 2024 | 2024-01-01 | 2024-06-30 | OPEN |
| HT Nhà ở cho NKT | → Quỹ ABC | 2024-03-01 | 2024-12-31 | OPEN |

#### Files test upload

| File | Kích thước | Mục đích |
|------|-----------|----------|
| cmnd_truoc.pdf | 500KB | Upload hợp lệ |
| giay_xac_nhan.png | 1MB | Upload hợp lệ |
| anh_chan_dung.jpg | 800KB | Upload hợp lệ |
| virus.exe | 100KB | Upload bị từ chối |
| file_lon.pdf | 15MB | Vượt kích thước |
