# Test Cases Chuyên Sâu — Luồng Duyệt, Từ Chối & Chi Trả Trợ Cấp

> Tổng số: **35 test cases** | Ngày tạo: 2026-03-24
> Luồng: `SUBMITTED → UNDER_REVIEW → APPROVED/REJECTED → PAID (qua Payment)`

---

## A. Xét Duyệt Hồ Sơ (SUBMITTED → UNDER_REVIEW)

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| WF-01 | Bắt đầu xét duyệt | Hồ sơ SUBMITTED, đăng nhập OFFICER | id hồ sơ SUBMITTED | 1. PATCH /api/applications/{id}/under-review 2. GET /api/applications/{id} 3. GET /api/notifications (CITIZEN) | trangThai="UNDER_REVIEW", reviewedBy=officer1, có thông báo INFO "Hồ sơ đang được xét duyệt" | Cao | Business Rule |
| WF-02 | Xét duyệt bằng ADMIN | Hồ sơ SUBMITTED, đăng nhập ADMIN | Token ADMIN | 1. PATCH /api/applications/{id}/under-review | trangThai="UNDER_REVIEW", reviewedBy=admin | Trung bình | Positive |
| WF-03 | Xét duyệt hồ sơ DRAFT | Hồ sơ DRAFT, đăng nhập OFFICER | id hồ sơ DRAFT | 1. PATCH /api/applications/{id}/under-review | 400 "Chỉ hồ sơ ở trạng thái SUBMITTED mới được chuyển sang UNDER_REVIEW. Trạng thái hiện tại: DRAFT" | Cao | Business Rule |
| WF-04 | Xét duyệt hồ sơ APPROVED | Hồ sơ APPROVED | id hồ sơ APPROVED | 1. PATCH under-review | 400 "Trạng thái hiện tại: APPROVED" | Trung bình | Business Rule |
| WF-05 | Xét duyệt hồ sơ PAID | Hồ sơ PAID | id hồ sơ PAID | 1. PATCH under-review | 400 "Trạng thái hiện tại: PAID" | Trung bình | Business Rule |
| WF-06 | CITIZEN xét duyệt | Hồ sơ SUBMITTED, đăng nhập CITIZEN | Token CITIZEN | 1. PATCH under-review | 403 Forbidden | Cao | Permission |
| WF-07 | ACCOUNTANT xét duyệt | Hồ sơ SUBMITTED, đăng nhập ACCOUNTANT | Token ACCOUNTANT | 1. PATCH under-review | 403 Forbidden | Trung bình | Permission |

## B. Phê Duyệt Hồ Sơ (UNDER_REVIEW → APPROVED)

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| WF-08 | Phê duyệt thành công | Hồ sơ UNDER_REVIEW, đăng nhập OFFICER | id hồ sơ UNDER_REVIEW | 1. PATCH /api/applications/{id}/approve 2. GET /api/applications/{id} 3. GET /api/notifications (CITIZEN) | trangThai="APPROVED", approvedBy=officer1, approvedAt≠null, lyDoTuChoi=null, thông báo SUCCESS "Hồ sơ đã được phê duyệt" | Cao | Business Rule |
| WF-09 | Approve xóa lyDoTuChoi cũ | Hồ sơ UNDER_REVIEW có lyDoTuChoi="Thiếu CMND" (từ lần reject trước) | id hồ sơ có lyDoTuChoi != null | 1. PATCH /api/applications/{id}/approve 2. GET chi tiết | lyDoTuChoi = null (đã bị xóa), trangThai="APPROVED" | Cao | Business Rule |
| WF-10 | Approve hồ sơ SUBMITTED | Hồ sơ SUBMITTED | id hồ sơ SUBMITTED | 1. PATCH approve | 400 "Chỉ hồ sơ ở trạng thái UNDER_REVIEW mới được phê duyệt. Trạng thái hiện tại: SUBMITTED" | Cao | Business Rule |
| WF-11 | Approve hồ sơ DRAFT | Hồ sơ DRAFT | id hồ sơ DRAFT | 1. PATCH approve | 400 "Trạng thái hiện tại: DRAFT" | Trung bình | Business Rule |
| WF-12 | Approve hồ sơ REJECTED | Hồ sơ REJECTED | id hồ sơ REJECTED | 1. PATCH approve | 400 "Trạng thái hiện tại: REJECTED" | Trung bình | Business Rule |
| WF-13 | CITIZEN approve | Đăng nhập CITIZEN | Token CITIZEN | 1. PATCH approve | 403 Forbidden | Cao | Permission |

## C. Từ Chối Hồ Sơ (UNDER_REVIEW → REJECTED)

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| WF-14 | Từ chối thành công | Hồ sơ UNDER_REVIEW, đăng nhập OFFICER | lyDoTuChoi: "Thiếu giấy chứng nhận hộ nghèo, thiếu bản sao CMND" | 1. PATCH /api/applications/{id}/reject body: {lyDoTuChoi: "..."} 2. GET chi tiết 3. GET notifications CITIZEN | trangThai="REJECTED", lyDoTuChoi="Thiếu giấy...", thông báo WARNING "Hồ sơ bị từ chối. Lý do: ..." | Cao | Business Rule |
| WF-15 | Từ chối — thiếu lý do rỗng | Hồ sơ UNDER_REVIEW | lyDoTuChoi: "" | 1. PATCH reject body: {lyDoTuChoi: ""} | 400 "Lý do từ chối không được để trống" | Cao | Business Rule |
| WF-16 | Từ chối — không gửi body | Hồ sơ UNDER_REVIEW | Body: null | 1. PATCH reject không gửi body | 400 validation error | Trung bình | Boundary |
| WF-17 | Từ chối — chỉ có khoảng trắng | Hồ sơ UNDER_REVIEW | lyDoTuChoi: "   " | 1. PATCH reject body: {lyDoTuChoi: "   "} | 400 "Lý do từ chối không được để trống" | Trung bình | Boundary |
| WF-18 | Từ chối hồ sơ SUBMITTED | Hồ sơ SUBMITTED | lyDoTuChoi: "Thiếu tài liệu" | 1. PATCH reject | 400 "Chỉ hồ sơ ở trạng thái UNDER_REVIEW mới được từ chối" | Cao | Business Rule |
| WF-19 | CITIZEN từ chối | Đăng nhập CITIZEN | Token CITIZEN | 1. PATCH reject | 403 Forbidden | Cao | Permission |

## D. Chi Trả Trợ Cấp

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| WF-20 | Tạo payment cho hồ sơ APPROVED | Hồ sơ APPROVED, ACCOUNTANT, nguồn quỹ conLai=500tr | hoSoHoTroId, soTien: 5000000, phuongThuc: "BANK_TRANSFER" | 1. POST /api/payments body hợp lệ 2. Verify response | success, trangThai="PENDING", ngayChiTra=today | Cao | Positive |
| WF-21 | Tạo payment — hồ sơ SUBMITTED | Hồ sơ SUBMITTED, ACCOUNTANT | hoSoHoTroId (SUBMITTED) | 1. POST /api/payments | 400 "Chỉ tạo chi trả cho hồ sơ đã được phê duyệt (APPROVED). Trạng thái hiện tại: SUBMITTED" | Cao | Business Rule |
| WF-22 | Tạo payment — hồ sơ DRAFT | Hồ sơ DRAFT | hoSoHoTroId (DRAFT) | 1. POST /api/payments | 400 "Trạng thái hiện tại: DRAFT" | Trung bình | Business Rule |
| WF-23 | Tạo payment — CITIZEN | CITIZEN đăng nhập | Token CITIZEN | 1. POST /api/payments | 403 Forbidden | Cao | Permission |
| WF-24 | Tạo payment — OFFICER | OFFICER đăng nhập | Token OFFICER | 1. POST /api/payments | 403 Forbidden | Trung bình | Permission |
| WF-25 | Payment SUCCESS đầy đủ side-effects | Payment PENDING, nguồn quỹ conLai=500tr, daSuDung=100tr | trangThai: "SUCCESS" | 1. PATCH /api/payments/{id}/status {trangThai:"SUCCESS"} 2. GET /api/applications/{hoSoId} 3. GET /api/fund-sources/{nguonQuyId} 4. GET /api/programs/{ctId} 5. GET /api/notifications (CITIZEN) | (a) Payment trangThai="SUCCESS" (b) Hồ sơ trangThai="PAID" (c) nguonQuy.conLai=495tr, daSuDung=105tr (d) chuongTrinh.daChi tăng 5tr (e) Thông báo SUCCESS "Chi trả trợ cấp thành công...5,000,000đ" | Cao | Business Rule |
| WF-26 | Payment SUCCESS — vượt ngân sách | Payment PENDING soTien=10tr, nguồn quỹ conLai=5tr | trangThai: "SUCCESS" | 1. PATCH status SUCCESS | 400 "Ngân sách nguồn quỹ '...' không đủ. Còn lại: 5,000,000đ, cần: 10,000,000đ" | Cao | Business Rule |
| WF-27 | Payment SUCCESS — ngân sách vừa đủ | Payment PENDING soTien=5tr, nguồn quỹ conLai=5tr | trangThai: "SUCCESS" | 1. PATCH status SUCCESS 2. GET nguồn quỹ | Payment SUCCESS, nguonQuy.conLai=0, daSuDung tăng 5tr | Trung bình | Boundary |
| WF-28 | Payment FAILED | Payment PENDING | trangThai: "FAILED" | 1. PATCH /api/payments/{id}/status {trangThai:"FAILED"} 2. GET hồ sơ | Payment FAILED, hồ sơ vẫn APPROVED (không chuyển PAID) | Trung bình | Positive |
| WF-29 | Payment CANCELLED | Payment PENDING | trangThai: "CANCELLED" | 1. PATCH status CANCELLED 2. GET hồ sơ | Payment CANCELLED, hồ sơ vẫn APPROVED | Trung bình | Positive |
| WF-30 | Đổi trạng thái payment SUCCESS | Payment đã SUCCESS | trangThai: "FAILED" | 1. PATCH status FAILED trên payment SUCCESS | 400 "Không thể thay đổi trạng thái giao dịch đã thành công" | Cao | Business Rule |
| WF-31 | Đổi trạng thái payment CANCELLED | Payment đã CANCELLED | trangThai: "SUCCESS" | 1. PATCH status SUCCESS trên payment CANCELLED | 400 "Không thể thay đổi trạng thái giao dịch đã huỷ" | Trung bình | Business Rule |
| WF-32 | Trạng thái không hợp lệ | Payment PENDING | trangThai: "COMPLETED" | 1. PATCH status "COMPLETED" | 400 "Trạng thái không hợp lệ: COMPLETED. Hợp lệ: [PENDING, SUCCESS, FAILED, CANCELLED]" | Trung bình | Negative |

## E. Luồng End-to-End

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| WF-33 | E2E: Duyệt → Chi trả thành công | CITIZEN, OFFICER, ACCOUNTANT đều đăng nhập. Nguồn quỹ conLai=500tr | Dữ liệu hồ sơ + payment hợp lệ | 1. CITIZEN POST tạo hồ sơ (→DRAFT) 2. CITIZEN PATCH submit (→SUBMITTED) 3. OFFICER PATCH under-review (→UNDER_REVIEW) 4. OFFICER PATCH approve (→APPROVED) 5. ACCOUNTANT POST tạo payment (→PENDING) 6. ACCOUNTANT PATCH payment SUCCESS 7. Verify: hồ sơ = PAID, nguồn quỹ giảm, 3 thông báo | 6 chuyển trạng thái thành công, 3 thông báo (under-review, approve, payment), ngân sách trừ đúng | Cao | Business Rule |
| WF-34 | E2E: Từ chối → Bổ sung → Duyệt lại → Chi trả | Hồ sơ SUBMITTED, OFFICER, ACCOUNTANT | lyDoTuChoi lần 1 + payment data | 1. OFFICER under-review (→UNDER_REVIEW) 2. OFFICER reject {lyDoTuChoi:"Thiếu CMND"} (→REJECTED) 3. Verify lyDoTuChoi="Thiếu CMND" 4. OFFICER under-review lại (→UNDER_REVIEW) 5. OFFICER approve (→APPROVED) 6. Verify lyDoTuChoi=null 7. ACCOUNTANT tạo + SUCCESS payment (→PAID) 8. Verify 5 thông báo | Luồng reject-reapprove-pay hoàn chỉnh, lyDoTuChoi bị xóa sau approve, 5 thông báo tổng | Cao | Business Rule |
| WF-35 | E2E: Chi trả vượt ngân sách nhiều hồ sơ | 2 hồ sơ APPROVED, nguồn quỹ conLai=8tr | Payment A: 5tr, Payment B: 5tr | 1. ACCOUNTANT tạo payment A (5tr) → SUCCESS → ngân sách còn 3tr 2. ACCOUNTANT tạo payment B (5tr) → SUCCESS | Payment A thành công (conLai=3tr). Payment B bị reject "Ngân sách không đủ. Còn lại: 3,000,000đ, cần: 5,000,000đ" | Cao | Business Rule |

---

## Tổng hợp

| Nhóm | Số TC | Positive | Negative | Boundary | Permission | Business Rule |
|------|-------|----------|----------|----------|------------|---------------|
| Xét duyệt (→UNDER_REVIEW) | 7 | 1 | 0 | 0 | 2 | 4 |
| Phê duyệt (→APPROVED) | 6 | 0 | 0 | 0 | 1 | 5 |
| Từ chối (→REJECTED) | 6 | 0 | 0 | 2 | 1 | 3 |
| Chi trả (Payment) | 13 | 2 | 1 | 1 | 2 | 7 |
| E2E luồng đầy đủ | 3 | 0 | 0 | 0 | 0 | 3 |
| **Tổng** | **35** | **3** | **1** | **3** | **6** | **22** |

## Ma trận trạng thái — Coverage

### Hồ sơ

```
DRAFT ──submit──► SUBMITTED ──under-review──► UNDER_REVIEW ──approve──► APPROVED ──payment SUCCESS──► PAID
                                                           └──reject──► REJECTED ──under-review──► UNDER_REVIEW (loop)
```

| Chuyển trạng thái | Test hợp lệ | Test bị chặn |
|-------------------|-------------|--------------|
| SUBMITTED → UNDER_REVIEW | WF-01 ✅ | WF-03 (DRAFT→UR), WF-04 (APPROVED→UR), WF-05 (PAID→UR) |
| UNDER_REVIEW → APPROVED | WF-08 ✅ | WF-10 (SUBMITTED), WF-11 (DRAFT), WF-12 (REJECTED) |
| UNDER_REVIEW → REJECTED | WF-14 ✅ | WF-18 (SUBMITTED) |
| REJECTED → UNDER_REVIEW | WF-34 ✅ | — |
| APPROVED → PAID (qua payment) | WF-25/33 ✅ | WF-21 (SUBMITTED), WF-22 (DRAFT) |

### Payment

| Chuyển trạng thái | Test hợp lệ | Test bị chặn |
|-------------------|-------------|--------------|
| PENDING → SUCCESS | WF-25 ✅ | WF-26 (vượt ngân sách) |
| PENDING → FAILED | WF-28 ✅ | — |
| PENDING → CANCELLED | WF-29 ✅ | — |
| SUCCESS → * | — | WF-30 ❌ |
| CANCELLED → * | — | WF-31 ❌ |

## Checklist thông báo tự động

| Sự kiện | Loại thông báo | Test case |
|---------|---------------|-----------|
| Hồ sơ → UNDER_REVIEW | INFO | WF-01 |
| Hồ sơ → APPROVED | SUCCESS | WF-08 |
| Hồ sơ → REJECTED | WARNING | WF-14 |
| Payment → SUCCESS | SUCCESS | WF-25 |
| E2E đủ 3 thông báo | INFO + SUCCESS + SUCCESS | WF-33 |
| E2E đủ 5 thông báo | INFO + WARNING + INFO + SUCCESS + SUCCESS | WF-34 |
