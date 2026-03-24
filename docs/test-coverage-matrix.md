# Ma Trận Bao Phủ Kiểm Thử Nghiệp Vụ

> Ngày tạo: 2026-03-24 | Tổng: **65 business rules** | Không bao gồm AI Review
> Tham chiếu: test-cases-auth.md, test-cases-ho-so-ho-tro.md, test-cases-duyet-chitrac.md, test-cases-upload-ocr.md, test-cases-nghiep-vu.md

---

## 1. Authentication

| Module | Chức năng | Business Rule | Test Cases | Ưu tiên | Bao phủ |
|--------|-----------|--------------|------------|---------|---------|
| Auth | Register | Username unique | AUTH-02 | Cao | ✅ |
| Auth | Register | Email unique | AUTH-03 | Cao | ✅ |
| Auth | Register | Validation: username, password, email bắt buộc | AUTH-04, 05, 06 | Trung bình | ✅ |
| Auth | Register | Password min 6 ký tự | AUTH-06 | Thấp | ✅ |
| Auth | Register | Luôn gán role CITIZEN (không cho client chọn) | AUTH-01 | Cao | ✅ |
| Auth | Login | Chung error message (chống user enumeration) | AUTH-08, 09 | Cao | ✅ |
| Auth | Login | Trả accessToken + refreshToken | AUTH-07 | Cao | ✅ |
| Auth | Token | Access token hết hạn → 401 | AUTH-12 | Cao | ✅ |
| Auth | Token | Token sai format → 401 | AUTH-13 | Cao | ✅ |
| Auth | Token | Không gửi token → 401 | AUTH-14 | Cao | ✅ |
| Auth | Refresh | Refresh token rotation | AUTH-15 | Cao | ✅ |
| Auth | Refresh | Refresh token không hợp lệ → 401 | AUTH-16 | Cao | ✅ |
| Auth | Logout | Revoke refresh token | AUTH-17 | Trung bình | ✅ |

## 2. Phân Quyền (RBAC)

| Module | Chức năng | Business Rule | Test Cases | Ưu tiên | Bao phủ |
|--------|-----------|--------------|------------|---------|---------|
| RBAC | ADMIN | Full quyền: user, fund, approve, payment | PERM-01→04 | Cao | ✅ |
| RBAC | OFFICER | Duyệt/từ chối hồ sơ, OCR | WF-01, WF-08, WF-14, OCR-01 | Cao | ✅ |
| RBAC | ACCOUNTANT | Tạo + cập nhật payment | WF-20, WF-25 | Cao | ✅ |
| RBAC | CITIZEN | Chỉ tạo/xem hồ sơ + upload tài liệu | HS-01, UPL-01 | Cao | ✅ |
| RBAC | CITIZEN | Không duyệt hồ sơ | PERM-06, WF-06 | Cao | ✅ |
| RBAC | CITIZEN | Không tạo payment | PERM-07, WF-23 | Cao | ✅ |
| RBAC | CITIZEN | Không quản lý user/fund | PERM-05, 08 | Cao | ✅ |
| RBAC | OFFICER | Không tạo payment | PERM-09, WF-24 | Trung bình | ✅ |
| RBAC | ACCOUNTANT | Không duyệt hồ sơ | PERM-10 | Trung bình | ✅ |
| RBAC | Ownership | CITIZEN chỉ xem hồ sơ/tài liệu của mình | PERM-11, 12, HS-37, UPL-11, 16, 18 | Cao | ✅ |

## 3. Đối Tượng Hưởng Trợ Cấp

| Module | Chức năng | Business Rule | Test Cases | Ưu tiên | Bao phủ |
|--------|-----------|--------------|------------|---------|---------|
| DoiTuong | CRUD | tenDoiTuong không rỗng | DT-INV-01 (test-data) | Trung bình | ✅ |
| DoiTuong | CRUD | Chỉ ADMIN/OFFICER quản lý | TC-nghiep-vu #16-20 | Trung bình | ✅ |
| DoiTuong | Search | Phân trang + tìm theo tên | TC-nghiep-vu #18 | Thấp | ✅ |

## 4. Nguồn Quỹ

| Module | Chức năng | Business Rule | Test Cases | Ưu tiên | Bao phủ |
|--------|-----------|--------------|------------|---------|---------|
| NguonQuy | CRUD | tongNganSach > 0 | NQ-INV-02 (test-data) | Trung bình | ✅ |
| NguonQuy | CRUD | Chỉ ADMIN quản lý | PERM-02, PERM-05 | Cao | ✅ |
| NguonQuy | Budget | conLai = tongNganSach - daSuDung | WF-25 | Cao | ✅ |

## 5. Chương Trình Trợ Cấp

| Module | Chức năng | Business Rule | Test Cases | Ưu tiên | Bao phủ |
|--------|-----------|--------------|------------|---------|---------|
| ChuongTrinh | Tạo | ngayBatDau <= ngayKetThuc | CT-INV-02 (test-data) | Trung bình | ✅ |
| ChuongTrinh | Tạo | nguonQuyId phải tồn tại | CT-INV-03 (test-data) | Trung bình | ✅ |
| ChuongTrinh | Tạo | trangThai: UPCOMING, OPEN, CLOSED | CT-01→03 (test-data) | Trung bình | ✅ |
| ChuongTrinh | CRUD | Chỉ ADMIN/OFFICER quản lý | TC-nghiep-vu #26-30 | Trung bình | ✅ |

## 6. Hồ Sơ Hỗ Trợ

| Module | Chức năng | Business Rule | Test Cases | Ưu tiên | Bao phủ |
|--------|-----------|--------------|------------|---------|---------|
| HoSo | Tạo | Mặc định DRAFT | HS-01 | Cao | ✅ |
| HoSo | Tạo | Validate doiTuongId tồn tại | HS-02 | Cao | ✅ |
| HoSo | Tạo | Validate chuongTrinhId tồn tại | HS-03 | Cao | ✅ |
| HoSo | Submit | Chỉ submit từ DRAFT | HS-06, 09, 10 | Cao | ✅ |
| HoSo | Submit | Validate required fields trước submit | HS-07, 08 | Cao | ✅ |
| HoSo | Submit | CITIZEN chỉ submit hồ sơ mình | HS-11 | Cao | ✅ |
| HoSo | Update | Chỉ sửa khi DRAFT | HS-27, 28 | Trung bình | ✅ |
| HoSo | Delete | Chỉ xóa khi DRAFT + ownership | HS-29, 30 | Trung bình | ✅ |
| HoSo | Search | Multi-filter AND (Criteria API) | HS-31→36 | Trung bình | ✅ |
| HoSo | Search | CITIZEN auto-filter userId | HS-37, 38, PERM-12 | Cao | ✅ |
| HoSo | Detail | CITIZEN chỉ xem hồ sơ mình | PERM-11 | Cao | ✅ |

## 7. Luồng Duyệt / Từ Chối

| Module | Chức năng | Business Rule | Test Cases | Ưu tiên | Bao phủ |
|--------|-----------|--------------|------------|---------|---------|
| Workflow | Under Review | Chỉ từ SUBMITTED hoặc REJECTED | WF-01, 03, 04, 05 | Cao | ✅ |
| Workflow | Under Review | Chỉ OFFICER/ADMIN | WF-06, 07, PERM-06 | Cao | ✅ |
| Workflow | Approve | Chỉ từ UNDER_REVIEW | WF-08, 10, 11, 12 | Cao | ✅ |
| Workflow | Approve | Xóa lyDoTuChoi cũ | WF-09, HS-20 | Cao | ✅ |
| Workflow | Reject | Chỉ từ UNDER_REVIEW | WF-14, 18 | Cao | ✅ |
| Workflow | Reject | Bắt buộc có lyDoTuChoi | WF-15, 16, 17 | Cao | ✅ |
| Workflow | Re-review | REJECTED → UNDER_REVIEW (bổ sung) | WF-34 | Cao | ✅ |
| Workflow | Notification | Tạo thông báo INFO khi under-review | WF-01 | Trung bình | ✅ |
| Workflow | Notification | Tạo thông báo SUCCESS khi approve | WF-08, WF-33 | Trung bình | ✅ |
| Workflow | Notification | Tạo thông báo WARNING khi reject | WF-14 | Trung bình | ✅ |

## 8. Tài Liệu & OCR

| Module | Chức năng | Business Rule | Test Cases | Ưu tiên | Bao phủ |
|--------|-----------|--------------|------------|---------|---------|
| TaiLieu | Upload | Chỉ pdf/png/jpg/jpeg | UPL-01→07 | Cao | ✅ |
| TaiLieu | Upload | Hồ sơ phải tồn tại | UPL-10 | Cao | ✅ |
| TaiLieu | Upload | Chủ hồ sơ hoặc OFFICER | UPL-11, 12, 13 | Cao | ✅ |
| TaiLieu | Upload | File không rỗng | UPL-08 | Trung bình | ✅ |
| TaiLieu | Detail | Ownership check | UPL-19 (fixed) | Cao | ✅ |
| TaiLieu | Delete | Ownership + xóa file disk | UPL-17, 18 | Trung bình | ✅ |
| OCR | Process | Chỉ OFFICER/ADMIN | OCR-05 | Cao | ✅ |
| OCR | Process | Chỉ file pdf/png/jpg/jpeg | OCR-04 | Trung bình | ✅ |
| OCR | Process | Lưu ketQuaOcr vào MongoDB | OCR-06 | Cao | ✅ |

## 9. Chi Trả

| Module | Chức năng | Business Rule | Test Cases | Ưu tiên | Bao phủ |
|--------|-----------|--------------|------------|---------|---------|
| ChiTra | Tạo | Chỉ cho hồ sơ APPROVED | WF-20, 21, 22 | Cao | ✅ |
| ChiTra | Tạo | Chỉ ACCOUNTANT/ADMIN | WF-23, 24 | Cao | ✅ |
| ChiTra | SUCCESS | Hồ sơ → PAID | WF-25 | Cao | ✅ |
| ChiTra | SUCCESS | Trừ ngân sách nguồn quỹ | WF-25 | Cao | ✅ |
| ChiTra | SUCCESS | Không vượt ngân sách | WF-26, 27 | Cao | ✅ |
| ChiTra | SUCCESS | Cập nhật daChi chương trình | WF-25 | Trung bình | ✅ |
| ChiTra | SUCCESS | Tạo thông báo cho CITIZEN | WF-25, 33 | Trung bình | ✅ |
| ChiTra | Immutable | Không đổi sau SUCCESS | WF-30 | Cao | ✅ |
| ChiTra | Immutable | Không đổi sau CANCELLED | WF-31 | Trung bình | ✅ |
| ChiTra | Immutable | Không đổi sau FAILED | WF-28 (fixed) | Trung bình | ✅ |
| ChiTra | FAILED | Hồ sơ vẫn APPROVED (không đổi) | WF-28 | Trung bình | ✅ |
| ChiTra | Race | 2 payment liên tiếp cạn ngân sách | WF-35 | Cao | ✅ |

## 10. Thông Báo

| Module | Chức năng | Business Rule | Test Cases | Ưu tiên | Bao phủ |
|--------|-----------|--------------|------------|---------|---------|
| ThongBao | Ownership | CITIZEN chỉ xem thông báo mình | TC-nghiep-vu #85-95 | Cao | ✅ |
| ThongBao | Tạo thủ công | Chỉ ADMIN tạo | TC-nghiep-vu #87 | Trung bình | ✅ |
| ThongBao | Filter | Lọc daDoc, userId, phân trang | TC-nghiep-vu #89-91 | Trung bình | ✅ |
| ThongBao | Mark read | Đánh dấu đã đọc | TC-nghiep-vu #88 | Thấp | ✅ |
| ThongBao | Auto-create | Tự động tạo khi approve/reject/payment | WF-01, 08, 14, 25 | Cao | ✅ |

---

## Tổng hợp bao phủ

| Module | Số Rules | Cao | Trung bình | Thấp | Bao phủ |
|--------|----------|-----|------------|------|---------|
| Auth | 13 | 10 | 2 | 1 | 13/13 ✅ |
| RBAC | 10 | 8 | 2 | 0 | 10/10 ✅ |
| Đối tượng | 3 | 0 | 2 | 1 | 3/3 ✅ |
| Nguồn quỹ | 3 | 2 | 1 | 0 | 3/3 ✅ |
| Chương trình | 4 | 0 | 4 | 0 | 4/4 ✅ |
| Hồ sơ | 11 | 8 | 3 | 0 | 11/11 ✅ |
| Duyệt/Từ chối | 10 | 7 | 3 | 0 | 10/10 ✅ |
| Tài liệu & OCR | 9 | 5 | 4 | 0 | 9/9 ✅ |
| Chi trả | 12 | 7 | 5 | 0 | 12/12 ✅ |
| Thông báo | 5 | 2 | 2 | 1 | 5/5 ✅ |
| **Tổng** | **80** | **49** | **28** | **3** | **80/80 ✅** |

## Tổng hợp test cases theo file

| File | Số TC | Module chính |
|------|-------|-------------|
| test-cases-auth.md | 30 | Auth, RBAC, Token |
| test-cases-ho-so-ho-tro.md | 40 | Hồ sơ, Submit, Search |
| test-cases-duyet-chitrac.md | 35 | Duyệt, Từ chối, Chi trả, E2E |
| test-cases-upload-ocr.md | 25 | Upload, OCR |
| test-cases-nghiep-vu.md | 95 | Tất cả (tổng quan) |
| **Tổng** | **225** | — |
