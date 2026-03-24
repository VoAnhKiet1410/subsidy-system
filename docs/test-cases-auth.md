# Test Cases Chuyên Sâu — Authentication & Authorization

> Tổng số: **30 test cases** | Ngày tạo: 2026-03-24
> Roles: `ADMIN`, `OFFICER`, `ACCOUNTANT`, `CITIZEN`

---

## A. Đăng Ký (Register)

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| AUTH-01 | Đăng ký thành công | Không có user cùng username/email | username: "citizen01", email: "c01@mail.com", password: "Abc@1234", hoTen: "Nguyễn Văn A" | 1. POST /api/auth/register 2. Verify response 3. Login với tài khoản vừa tạo | success=true, user có role=CITIZEN mặc định, login thành công | Cao | Positive |
| AUTH-02 | Đăng ký — trùng username | Đã tồn tại user "citizen01" | username: "citizen01", email: "new@mail.com" | 1. POST /api/auth/register | 400 "Username đã tồn tại" | Cao | Negative |
| AUTH-03 | Đăng ký — trùng email | Đã tồn tại user email "c01@mail.com" | username: "newuser", email: "c01@mail.com" | 1. POST /api/auth/register | 400 "Email đã được sử dụng" | Cao | Negative |
| AUTH-04 | Đăng ký — thiếu username | Không có user | username: "", password: "Abc@1234" | 1. POST /api/auth/register thiếu username | 400 validation error "Username không được để trống" | Trung bình | Boundary |
| AUTH-05 | Đăng ký — thiếu password | Không có user | username: "test01", password: "" | 1. POST /api/auth/register | 400 "Password không được để trống" | Trung bình | Boundary |
| AUTH-06 | Đăng ký — password quá ngắn | Không có user | username: "test01", password: "123" | 1. POST /api/auth/register | 400 "Password phải có ít nhất 6 ký tự" | Thấp | Boundary |

## B. Đăng Nhập (Login)

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| AUTH-07 | Login thành công | User "citizen01" tồn tại, password: "Abc@1234" | username: "citizen01", password: "Abc@1234" | 1. POST /api/auth/login 2. Verify response | success=true, accessToken != null, refreshToken != null, user info (id, username, roles) | Cao | Positive |
| AUTH-08 | Login — sai password | User "citizen01" tồn tại | username: "citizen01", password: "wrongpass" | 1. POST /api/auth/login | 401 "Sai username hoặc password" | Cao | Negative |
| AUTH-09 | Login — sai username | Không có user "unknown" | username: "unknown", password: "Abc@1234" | 1. POST /api/auth/login | 401 "Sai username hoặc password" | Cao | Negative |
| AUTH-10 | Login — thiếu body | Không gửi body | Body: null | 1. POST /api/auth/login không gửi body | 400 validation error | Trung bình | Boundary |

## C. Token Management

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| AUTH-11 | Access token hợp lệ | User đã login, có accessToken | accessToken hợp lệ | 1. GET /api/applications kèm header Authorization: Bearer {accessToken} | 200 OK, trả dữ liệu | Cao | Positive |
| AUTH-12 | Access token hết hạn | Token đã expire | accessToken hết hạn | 1. GET /api/applications kèm token hết hạn | 401 Unauthorized "Token đã hết hạn" | Cao | Security |
| AUTH-13 | Access token sai format | Token bị sửa | accessToken: "invalid.token.here" | 1. GET /api/applications kèm token sai | 401 Unauthorized | Cao | Security |
| AUTH-14 | Không gửi token | Không login | Không có Authorization header | 1. GET /api/applications không kèm token | 401 Unauthorized | Cao | Security |
| AUTH-15 | Refresh token thành công | User đã login, có refreshToken | refreshToken hợp lệ | 1. POST /api/auth/refresh-token {refreshToken} 2. Verify response | success=true, accessToken mới != null, refreshToken mới != null | Cao | Positive |
| AUTH-16 | Refresh token không hợp lệ | Token bị sửa hoặc revoke | refreshToken: "invalid-refresh" | 1. POST /api/auth/refresh-token | 400/401 "Refresh token không hợp lệ" | Cao | Security |
| AUTH-17 | Logout | User đã login | accessToken + refreshToken | 1. POST /api/auth/logout 2. Thử dùng refreshToken cũ để refresh | Logout thành công, refresh token cũ bị vô hiệu (refresh thất bại) | Trung bình | Positive |

## D. Phân Quyền — ADMIN

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| PERM-01 | ADMIN quản lý user | ADMIN đăng nhập | Token ADMIN | 1. GET /api/users 2. PUT /api/users/{id} 3. DELETE /api/users/{id} | Tất cả 200 OK — ADMIN có full quyền quản lý user | Cao | Permission |
| PERM-02 | ADMIN quản lý nguồn quỹ | ADMIN đăng nhập | Token ADMIN | 1. POST /api/fund-sources 2. PUT /api/fund-sources/{id} 3. DELETE /api/fund-sources/{id} | Tất cả 200 OK | Trung bình | Permission |
| PERM-03 | ADMIN duyệt hồ sơ | ADMIN đăng nhập, hồ sơ SUBMITTED | Token ADMIN | 1. PATCH /api/applications/{id}/under-review 2. PATCH approve | Cả hai thành công — ADMIN có quyền duyệt | Trung bình | Permission |
| PERM-04 | ADMIN quản lý payment | ADMIN đăng nhập | Token ADMIN | 1. POST /api/payments 2. PATCH /api/payments/{id}/status | Cả hai thành công — ADMIN có quyền payment | Trung bình | Permission |

## E. Phân Quyền — Chặn Truy Cập Trái Phép

| TC ID | Chức năng | Preconditions | Test Data | Steps | Expected Result | Priority | Test Type |
|-------|-----------|---------------|-----------|-------|-----------------|----------|-----------|
| PERM-05 | CITIZEN tạo nguồn quỹ | CITIZEN đăng nhập | Token CITIZEN | 1. POST /api/fund-sources | 403 Forbidden | Cao | Permission |
| PERM-06 | CITIZEN duyệt hồ sơ | CITIZEN đăng nhập, hồ sơ SUBMITTED | Token CITIZEN | 1. PATCH /api/applications/{id}/under-review | 403 Forbidden | Cao | Permission |
| PERM-07 | CITIZEN tạo payment | CITIZEN đăng nhập | Token CITIZEN | 1. POST /api/payments | 403 Forbidden | Cao | Permission |
| PERM-08 | CITIZEN quản lý user | CITIZEN đăng nhập | Token CITIZEN | 1. GET /api/users | 403 Forbidden | Cao | Permission |
| PERM-09 | OFFICER tạo payment | OFFICER đăng nhập | Token OFFICER | 1. POST /api/payments | 403 Forbidden | Trung bình | Permission |
| PERM-10 | ACCOUNTANT duyệt hồ sơ | ACCOUNTANT đăng nhập | Token ACCOUNTANT | 1. PATCH /api/applications/{id}/approve | 403 Forbidden | Trung bình | Permission |
| PERM-11 | CITIZEN xem hồ sơ người khác | CITIZEN B, hồ sơ A | Token B, id hồ sơ A | 1. GET /api/applications/{idA} | 400 "Bạn không có quyền xem hồ sơ này" | Cao | Permission |
| PERM-12 | CITIZEN search thấy hồ sơ mình | CITIZEN A có 3 HS, B có 2 HS | Token CITIZEN A | 1. GET /api/applications | Trả đúng 3 hồ sơ của A, không thấy B | Cao | Permission |
| PERM-13 | OFFICER xem tất cả hồ sơ | OFFICER, có 10 hồ sơ nhiều user | Token OFFICER | 1. GET /api/applications | Trả tất cả 10 hồ sơ | Trung bình | Permission |

---

## Tổng hợp

| Nhóm | Số TC | Positive | Negative | Boundary | Permission | Security |
|------|-------|----------|----------|----------|------------|----------|
| Đăng ký | 6 | 1 | 2 | 3 | 0 | 0 |
| Đăng nhập | 4 | 1 | 2 | 1 | 0 | 0 |
| Token | 7 | 2 | 0 | 0 | 0 | 5 |
| ADMIN quyền | 4 | 0 | 0 | 0 | 4 | 0 |
| Chặn trái phép | 9 | 0 | 0 | 0 | 9 | 0 |
| **Tổng** | **30** | **4** | **4** | **4** | **13** | **5** |

## Ma trận phân quyền — Test Coverage

| API | ADMIN | OFFICER | ACCOUNTANT | CITIZEN |
|-----|-------|---------|------------|---------|
| GET /api/users | PERM-01 ✅ | — | — | PERM-08 ❌ |
| POST /api/fund-sources | PERM-02 ✅ | — | — | PERM-05 ❌ |
| PATCH approve/reject | PERM-03 ✅ | WF-08 ✅ | PERM-10 ❌ | PERM-06 ❌ |
| POST /api/payments | PERM-04 ✅ | PERM-09 ❌ | WF-20 ✅ | PERM-07 ❌ |
| GET /api/applications | ✅ all | PERM-13 ✅ | — | PERM-12 (own only) |
| GET /api/applications/{id} | ✅ all | ✅ all | ✅ all | PERM-11 (own only) |

> ✅ = có quyền (test positive) | ❌ = bị chặn 403 (test negative)
