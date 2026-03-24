# Dữ Liệu Test Mẫu — Hệ Thống Quản Lý Trợ Cấp Xã Hội

> Ngày tạo: 2026-03-24 | Phục vụ kiểm thử nghiệp vụ toàn bộ hệ thống
> Không bao gồm AI Review

---

## 1. Users

| Mã | Username | Password | Email | Họ tên | Role | Mục đích |
|----|----------|----------|-------|--------|------|----------|
| U-01 | admin | Admin@123 | admin@trocap.vn | Quản trị viên | ADMIN | Test full quyền: quản lý user, fund, duyệt, payment |
| U-02 | officer01 | Officer@123 | officer01@trocap.vn | Trần Văn B | OFFICER | Test duyệt/từ chối hồ sơ, OCR |
| U-03 | accountant01 | Account@123 | acc01@trocap.vn | Lê Thị C | ACCOUNTANT | Test tạo/cập nhật payment |
| U-04 | citizen01 | Citizen@123 | citizen01@mail.com | Nguyễn Văn A | CITIZEN | Test tạo hồ sơ, ownership, upload |
| U-05 | citizen02 | Citizen@123 | citizen02@mail.com | Phạm Thị D | CITIZEN | Test ownership (xem hồ sơ người khác) |
| U-06 | citizen03 | Citizen@123 | citizen03@mail.com | Hoàng Văn E | CITIZEN | Test CITIZEN không có hồ sơ |

### Dữ liệu không hợp lệ (Register)

| Mã | Username | Password | Email | Mục đích |
|----|----------|----------|-------|----------|
| U-INV-01 | admin | NewPass@1 | new@mail.com | Trùng username → 400 |
| U-INV-02 | newuser | NewPass@1 | admin@trocap.vn | Trùng email → 400 |
| U-INV-03 | "" | Abc@1234 | test@mail.com | Username rỗng → validation error |
| U-INV-04 | testuser | 123 | test@mail.com | Password < 6 ký tự → validation error |
| U-INV-05 | testuser | Abc@1234 | not-an-email | Email sai format → validation error |

### Dữ liệu không hợp lệ (Login)

| Mã | Username | Password | Mục đích |
|----|----------|----------|----------|
| L-INV-01 | citizen01 | WrongPass | Sai password → 401 |
| L-INV-02 | nonexistent | Abc@1234 | Username không tồn tại → 401 |
| L-INV-03 | "" | "" | Thiếu field → 400 |

---

## 2. Đối Tượng Hưởng Trợ Cấp (Beneficiary Groups)

| Mã | tenDoiTuong | moTa | Mục đích |
|----|-------------|------|----------|
| DT-01 | Hộ nghèo | Hộ gia đình có thu nhập dưới chuẩn nghèo | Test tạo hồ sơ, lọc theo đối tượng |
| DT-02 | Người khuyết tật | Người có giấy xác nhận khuyết tật | Test tạo hồ sơ khác đối tượng |
| DT-03 | Trẻ em mồ côi | Trẻ em dưới 16 tuổi không còn cha mẹ | Test danh sách, phân trang |
| DT-04 | Người cao tuổi | Người từ 60 tuổi trở lên | Test search, filter |

### Dữ liệu không hợp lệ

| Mã | tenDoiTuong | Mục đích |
|----|-------------|----------|
| DT-INV-01 | "" | Tên rỗng → validation error |
| DT-INV-02 | "A" | Tên quá ngắn (nếu có min length) |

---

## 3. Nguồn Quỹ (Fund Sources)

| Mã | tenNguonQuy | tongNganSach | daSuDung | conLai | Mục đích |
|----|-------------|-------------|----------|--------|----------|
| NQ-01 | Quỹ XHCN Quốc gia | 500,000,000 | 100,000,000 | 400,000,000 | Test payment success, trừ ngân sách |
| NQ-02 | Quỹ Bảo trợ Trẻ em | 50,000,000 | 45,000,000 | 5,000,000 | Test boundary: vượt ngân sách |
| NQ-03 | Quỹ Hỗ trợ Thiên tai | 200,000,000 | 0 | 200,000,000 | Test quỹ mới chưa sử dụng |

### Dữ liệu không hợp lệ

| Mã | tenNguonQuy | tongNganSach | Mục đích |
|----|-------------|-------------|----------|
| NQ-INV-01 | "" | 100000000 | Tên rỗng → validation error |
| NQ-INV-02 | Quỹ Test | -500000 | Ngân sách âm → validation error |
| NQ-INV-03 | Quỹ Test | 0 | Ngân sách = 0 → boundary |

---

## 4. Chương Trình Trợ Cấp (Subsidy Programs)

| Mã | tenChuongTrinh | nguonQuyId | ngayBatDau | ngayKetThuc | trangThai | Mục đích |
|----|----------------|-----------|-----------|------------|-----------|----------|
| CT-01 | Hỗ trợ hộ nghèo 2024 | NQ-01 | 2024-01-01 | 2024-12-31 | OPEN | Test tạo hồ sơ, liên kết quỹ |
| CT-02 | Trợ cấp khuyết tật Q1 | NQ-02 | 2024-01-01 | 2024-03-31 | CLOSED | Test lọc theo trạng thái |
| CT-03 | Hỗ trợ trẻ mồ côi 2025 | NQ-03 | 2025-01-01 | 2025-06-30 | UPCOMING | Test lọc UPCOMING |
| CT-04 | Chương trình đặc biệt | NQ-01 | 2024-06-01 | 2024-12-31 | OPEN | Test đa chương trình cùng quỹ |

### Dữ liệu không hợp lệ

| Mã | tenChuongTrinh | nguonQuyId | ngayBatDau | ngayKetThuc | Mục đích |
|----|----------------|-----------|-----------|------------|----------|
| CT-INV-01 | "" | NQ-01 | 2024-01-01 | 2024-12-31 | Tên rỗng → validation |
| CT-INV-02 | Test | NQ-01 | 2024-12-31 | 2024-01-01 | ngayBatDau > ngayKetThuc → validation |
| CT-INV-03 | Test | INVALID_ID | 2024-01-01 | 2024-12-31 | nguonQuyId không tồn tại → 400 |

---

## 5. Hồ Sơ Hỗ Trợ (Applications)

| Mã | nguoiDungId | doiTuongId | chuongTrinhId | trangThai | moTa | lyDoTuChoi | Mục đích |
|----|------------|-----------|--------------|-----------|------|-----------|----------|
| HS-01 | U-04 | DT-01 | CT-01 | DRAFT | Xin hỗ trợ hộ nghèo | null | Test update, submit |
| HS-02 | U-04 | DT-01 | CT-01 | SUBMITTED | Đã nộp hồ sơ | null | Test under-review |
| HS-03 | U-04 | DT-02 | CT-01 | UNDER_REVIEW | Đang xét duyệt | null | Test approve/reject |
| HS-04 | U-04 | DT-01 | CT-01 | APPROVED | Đã duyệt | null | Test tạo payment |
| HS-05 | U-04 | DT-01 | CT-01 | REJECTED | Bị từ chối | "Thiếu CMND" | Test re-review |
| HS-06 | U-04 | DT-01 | CT-01 | PAID | Đã chi trả | null | Test chặn thay đổi |
| HS-07 | U-05 | DT-02 | CT-01 | DRAFT | Hồ sơ CITIZEN B | null | Test ownership (CITIZEN A không xem được) |
| HS-08 | U-05 | DT-03 | CT-04 | SUBMITTED | Hồ sơ khác chương trình | null | Test filter chuongTrinhId |

### Dữ liệu không hợp lệ

| Mã | doiTuongId | chuongTrinhId | Mục đích |
|----|-----------|--------------|----------|
| HS-INV-01 | INVALID_ID | CT-01 | Đối tượng không tồn tại → 400 |
| HS-INV-02 | DT-01 | INVALID_ID | Chương trình không tồn tại → 400 |
| HS-INV-03 | null | CT-01 | Submit thiếu đối tượng → 400 |
| HS-INV-04 | DT-01 | null | Submit thiếu chương trình → 400 |

---

## 6. Tài Liệu Đính Kèm (Attachments)

| Mã | hoSoHoTroId | File | loaiFile | kichThuoc | Mục đích |
|----|------------|------|---------|-----------|----------|
| TL-01 | HS-01 | cmnd_scan.pdf | application/pdf | 500KB | Upload + OCR hợp lệ |
| TL-02 | HS-01 | giay_xacnhan.png | image/png | 1MB | Upload + OCR image |
| TL-03 | HS-02 | ho_khau.jpg | image/jpg | 800KB | Upload JPG hợp lệ |
| TL-04 | HS-03 | bien_ban.jpeg | image/jpeg | 600KB | Upload JPEG hợp lệ |

### Dữ liệu không hợp lệ

| Mã | File | loaiFile | Mục đích |
|----|------|---------|----------|
| TL-INV-01 | virus.exe | application/x-msdownload | Format bị chặn → 400 |
| TL-INV-02 | document.docx | application/vnd.openxml | Format bị chặn → 400 |
| TL-INV-03 | archive.zip | application/zip | Format bị chặn → 400 |
| TL-INV-04 | empty.pdf | — | File 0 bytes → 400 |
| TL-INV-05 | large.pdf | application/pdf | File 15MB > limit → 413 |

---

## 7. Chi Trả (Payments)

| Mã | hoSoHoTroId | soTien | phuongThuc | trangThai | Mục đích |
|----|------------|--------|-----------|-----------|----------|
| PAY-01 | HS-04 | 5,000,000 | BANK_TRANSFER | PENDING | Test cập nhật → SUCCESS |
| PAY-02 | HS-04 | 3,000,000 | CASH | PENDING | Test cập nhật → FAILED |
| PAY-03 | HS-04 | 2,000,000 | E_WALLET | SUCCESS | Test chặn thay đổi sau SUCCESS |
| PAY-04 | HS-04 | 1,000,000 | BANK_TRANSFER | CANCELLED | Test chặn thay đổi sau CANCELLED |
| PAY-05 | HS-04 | 1,500,000 | CASH | FAILED | Test chặn thay đổi sau FAILED |

### Dữ liệu không hợp lệ

| Mã | hoSoHoTroId | soTien | Mục đích |
|----|------------|--------|----------|
| PAY-INV-01 | HS-02 | 5000000 | Hồ sơ SUBMITTED (chưa approved) → 400 |
| PAY-INV-02 | HS-01 | 5000000 | Hồ sơ DRAFT → 400 |
| PAY-INV-03 | INVALID | 5000000 | Hồ sơ không tồn tại → 404 |
| PAY-INV-04 | HS-04 | -100000 | Số tiền âm → validation error |
| PAY-INV-05 | HS-04 | 0 | Số tiền = 0 → validation error |

### Dữ liệu test vượt ngân sách

| Mã | hoSoHoTroId | soTien | nguonQuy.conLai | Mục đích |
|----|------------|--------|----------------|----------|
| PAY-BUD-01 | HS-approved (CT→NQ-02) | 5,000,000 | 5,000,000 | Boundary: vừa đúng → SUCCESS |
| PAY-BUD-02 | HS-approved (CT→NQ-02) | 6,000,000 | 5,000,000 | Vượt ngân sách → 400 |
| PAY-BUD-03 | HS-approved (CT→NQ-02) | 5,000,001 | 5,000,000 | Vượt 1đ → 400 |

---

## 8. Thông Báo (Notifications)

| Mã | nguoiDungId | tieuDe | loai | daDoc | Mục đích |
|----|------------|--------|------|-------|----------|
| TB-01 | U-04 | Hồ sơ đang được xét duyệt | INFO | false | Test danh sách, filter daDoc |
| TB-02 | U-04 | Hồ sơ đã được phê duyệt | SUCCESS | false | Test mark as read |
| TB-03 | U-04 | Hồ sơ bị từ chối | WARNING | true | Test filter daDoc=true |
| TB-04 | U-04 | Chi trả trợ cấp thành công | SUCCESS | false | Test thông báo payment |
| TB-05 | U-05 | Hồ sơ đang xét duyệt | INFO | false | Test ownership (U-04 không thấy) |

---

## 9. Token & Auth Data

| Mã | Loại | Giá trị | Mục đích |
|----|------|---------|----------|
| TOKEN-01 | Access Token hợp lệ | (Lấy từ login U-04) | Test API call bình thường |
| TOKEN-02 | Access Token hết hạn | eyJ... (expired) | Test 401 token expired |
| TOKEN-03 | Token sai format | "invalid.token.here" | Test 401 invalid token |
| TOKEN-04 | Refresh Token hợp lệ | (Lấy từ login U-04) | Test refresh thành công |
| TOKEN-05 | Refresh Token đã revoke | (Token sau logout) | Test refresh thất bại |
| TOKEN-06 | Không có token | (Bỏ header Authorization) | Test 401 no token |

---

## Tổng hợp dữ liệu test

| Module | Hợp lệ | Không hợp lệ | Boundary | Tổng |
|--------|--------|--------------|----------|------|
| Users | 6 | 8 | 0 | 14 |
| Đối tượng | 4 | 2 | 0 | 6 |
| Nguồn quỹ | 3 | 3 | 0 | 6 |
| Chương trình | 4 | 3 | 0 | 7 |
| Hồ sơ | 8 | 4 | 0 | 12 |
| Tài liệu | 4 | 5 | 0 | 9 |
| Chi trả | 5 | 5 | 3 | 13 |
| Thông báo | 5 | 0 | 0 | 5 |
| Token | 6 | 0 | 0 | 6 |
| **Tổng** | **45** | **30** | **3** | **78** |

---

## Thứ tự tạo dữ liệu (Dependencies)

```
1. Users (U-01 → U-06)
   │
2. Đối tượng hưởng trợ cấp (DT-01 → DT-04)
   │
3. Nguồn quỹ (NQ-01 → NQ-03)
   │
4. Chương trình trợ cấp (CT-01 → CT-04)  ← cần NQ-*
   │
5. Hồ sơ hỗ trợ (HS-01 → HS-08)  ← cần U-*, DT-*, CT-*
   │
6. Tài liệu đính kèm (TL-01 → TL-04)  ← cần HS-*
   │
7. Chi trả (PAY-01 → PAY-05)  ← cần HS-* (APPROVED)
   │
8. Thông báo (TB-01 → TB-05)  ← tự động từ workflow
```
